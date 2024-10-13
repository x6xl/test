package top.hcode.hoj.manager.question;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusNotFoundException;
import top.hcode.hoj.dao.judge.JudgeEntityService;
import top.hcode.hoj.dao.question.*;
import top.hcode.hoj.manager.email.EmailManager;
import top.hcode.hoj.pojo.dto.examEmailDTO;
import top.hcode.hoj.pojo.dto.examFinishDTO;
import top.hcode.hoj.pojo.entity.judge.Judge;
import top.hcode.hoj.pojo.entity.question.*;
import top.hcode.hoj.pojo.vo.*;
import top.hcode.hoj.shiro.AccountProfile;
import top.hcode.hoj.utils.Constants;
import top.hcode.hoj.utils.RedisUtils;
import top.hcode.hoj.utils.examEmailUtils;
import top.hcode.hoj.validator.CommonValidator;


import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class examManager {
    @Autowired
    contentEntityService contentEntityService;

    @Autowired
    optionsEntityService optionsEntityService;

    @Autowired
    examEntityService examEntityService;

    @Autowired
    examRecordEntityService examRecordEntityService;

    @Autowired
    examPasswordEntityService examPasswordEntityService;

    @Autowired
    RedisUtils redisUtils;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    CommonValidator commonValidator;
    @Resource
    examAsyncManager examAsyncManager;

    public examVO getExam(String examId, String password) throws StatusNotFoundException, StatusFailException {
        String cacheKey = "exam:" + examId;
        String lockKey = "getExam:lock" + examId;
        exam cachedExam = (exam) redisUtils.get(cacheKey);
        exam exam;
        if (cachedExam == null) {
            boolean lockAcquire = redisUtils.getLock(lockKey, 10);
            try {
                if (lockAcquire) {
                    QueryWrapper<exam> examQueryWrapper1 = new QueryWrapper<>();
                    examQueryWrapper1
                            .eq("exam_id", examId);
                    exam = examEntityService.getOne(examQueryWrapper1);
                    if (exam == null) {
                        throw new StatusNotFoundException("获取失败，该试卷不存在！");
                    }
                    redisUtils.set(cacheKey, exam);
                } else {
                    Thread.sleep(1000);
                    exam = (exam) redisUtils.get(cacheKey);
                    if (exam == null) {
                        throw new StatusFailException("获取失败，该试卷不存在！");
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (lockAcquire) {
                    redisUtils.releaseLock(lockKey);
                }
            }
        } else {
            exam = cachedExam;
        }
//        String recordKey = "examPassword:" + examId;
//        String password1 = (String) redisUtils.get(recordKey);
//        if (password1 == null) {
//            // 如果 Redis 中没有该对象，则从数据库获取并缓存
//            QueryWrapper<examPassword> examPasswordQueryWrapper = new QueryWrapper<>();
//            examPasswordQueryWrapper.eq("exam_id", examId);
//            examPassword examPassword = examPasswordEntityService.getOne(examPasswordQueryWrapper);
//            if (examPassword != null) {
//                password1 = examPassword.getPassword();
//                redisUtils.set(recordKey, password1); // 缓存密码对象
//            } else {
//                throw new StatusFailException("未找到考试密码记录");
//            }
//        }
        CompletableFuture<String> examPassword = examAsyncManager.getExamPassword(examId);
        String password1=examPassword.join();

        if (!password1.equals(password)) {
            throw new StatusFailException("获取试题失败，密码错误！");
        }
        LocalDateTime currentDateTime = LocalDateTime.now();
        // 获取考试开始时间和结束时间
        LocalDateTime examStartDate = exam.getExamStartDate();
        LocalDateTime examEndDate = exam.getExamEndDate();

        // 判断当前时间是否在考试时间段内
        if (currentDateTime.isBefore(examStartDate) || currentDateTime.isAfter(examEndDate)) {
            throw new StatusFailException("当前不在考试时间段内");
        }

        examVO examVO = new examVO();
        examVO.setExam(exam);

        String examProblemIds = examVO.getExam().getProblemId();
        if (examProblemIds != null) {
            String[] problemIdsArray = examProblemIds.split(",");
            List<String> problemIds = new ArrayList<>();
            for (String problemId : problemIdsArray) {
                problemIds.add(problemId);
            }
            examVO.setProblemId(problemIds);
        }


        examVO.setSelectQuestionVOs(new ArrayList<>());

        String examQuestionIds = exam.getQuestionId();
        String[] questionIdsArray = examQuestionIds.split(",");

        List<CompletableFuture<examSelectQuestionVO>> futures = new ArrayList<>();
        for (String questionId : questionIdsArray) {
            CompletableFuture<examSelectQuestionVO> future = CompletableFuture.supplyAsync(() -> {
                examSelectQuestionVO selectQuestionVO = new examSelectQuestionVO();
                selectQuestionVO.setQuestionId(questionId);

                CompletableFuture<questionContentVO> questionContentFuture = examAsyncManager.getQuestionContentById(questionId);
                CompletableFuture<List<options>> optionsFuture = examAsyncManager.getOptionsByQuestionId(questionId);

                questionContentVO questionContentVO = questionContentFuture.join();
                selectQuestionVO.setQuestionContentVOs(Collections.singletonList(questionContentVO));

                if (questionContentVO.getQuestionType() == 1 || questionContentVO.getQuestionType() == 2) {
                    List<options> optionsList = optionsFuture.join();
                    selectQuestionVO.setOptions(optionsList);
                }

                return selectQuestionVO;
            });
            futures.add(future);
        }

        // 等待所有任务完成
        List<examSelectQuestionVO> selectQuestionVOs = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        examVO.setSelectQuestionVOs(selectQuestionVOs);
        return examVO;
    }

    public List<examRecordVO> getExamRecordList() throws StatusNotFoundException {
//        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        QueryWrapper<examRecord> examRecordQueryWrapper = new QueryWrapper<>();
        examRecordQueryWrapper
                .eq("exam_joiner_id", 1);
        List<examRecord> examRecords = examRecordEntityService.list(examRecordQueryWrapper);
        List<examRecordVO> examRecordVoList = new ArrayList<>();
        for (examRecord examRecord : examRecords) {
            examRecordVO examRecordVO = new examRecordVO();
            String cacheKey = "exam:" + examRecord.getExamId();
            exam cachedExam = (exam) redisUtils.get(cacheKey);

            exam exam;
            if (cachedExam == null) {
                QueryWrapper<exam> examQueryWrapper1 = new QueryWrapper<>();
                examQueryWrapper1
                        .eq("exam_id", examRecord.getExamId());
                exam = examEntityService.getOne(examQueryWrapper1);
                if (exam == null) {
                    throw new StatusNotFoundException("获取失败，该试卷不存在！");
                }
                redisUtils.set(cacheKey, exam);
            } else {
                exam = cachedExam;
            }
            examRecordVO.setExam(exam);
            examRecordVO.setExamRecord(examRecord);
            examRecordVoList.add(examRecordVO);
        }
        return examRecordVoList;
    }

    public examRecord judgeExam(String examId, examFinishDTO examFinishDTO) throws StatusNotFoundException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        String cacheKey = "exam:" + examId;
        exam cachedExam = (exam) redisUtils.get(cacheKey);
        String lockKey = "lock:exam:" + examId;

        exam exam;
        if (cachedExam == null) {
            //加上分布式锁，防止击穿缓存
            boolean lockAcquire = redisUtils.getLock(lockKey, 10);
            try {
                if (lockAcquire) {
                    QueryWrapper<exam> examQueryWrapper1 = new QueryWrapper<>();
                    examQueryWrapper1
                            .eq("exam_id", examId);
                    exam = examEntityService.getOne(examQueryWrapper1);
                    if (exam == null) {
                        throw new StatusNotFoundException("获取失败，该试卷不存在！");
                    }
                    redisUtils.set(cacheKey, exam);
                } else {
                    Thread.sleep(1000);
                    exam = (exam) redisUtils.get(cacheKey);
                    if (exam == null) {
                        throw new StatusNotFoundException("获取失败，该试卷不存在！");
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (lockAcquire) {
                    redisUtils.releaseLock(lockKey);
                }
            }
        } else {
            exam = cachedExam;
        }

        String examQuestionIds = exam.getQuestionId();
        String[] questionIdsArray = examQuestionIds.split(",");


        Date examJoinDate = examFinishDTO.getExamJoinDate();
        int examTimeCost = examFinishDTO.getExamTimeCost();
        int totalScore = 0;
        int radioScore = exam.getExamScoreRadio();
        int checkScore = exam.getExamScoreCheck();
        int judgeScore = exam.getExamScoreJudge();
        int fillScore = exam.getExamScoreFill();
        int codeScore = exam.getExamScoreCode();
        StringBuilder answerOptionIdsBuilder = new StringBuilder();

        String questionContentsCache = "judgeExam" + examId;
        String judgeLockKey = "judge:lock:" + examId;
        List<content> questionContents = (List<content>) redisUtils.get(questionContentsCache);
        if (questionContents == null) {
            //加入分布式锁，防止击穿缓存
            boolean lockAcquire = redisUtils.getLock(judgeLockKey, 10);
            try {
                if (lockAcquire) {
                    List<String> questionIdsList = Arrays.asList(questionIdsArray);
                    QueryWrapper<content> contentsQueryWrapper = new QueryWrapper<>();
                    contentsQueryWrapper.in("question_id", questionIdsList);
                    questionContents = contentEntityService.list(contentsQueryWrapper);
                    redisUtils.set(questionContentsCache, questionContents);
                } else {
                    Thread.sleep(1000);
                    questionContents = (List<content>) redisUtils.get(questionContentsCache);
                    if (questionContents == null) {
                        throw new StatusNotFoundException("判题失败，没找到考试对应的题目！");
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (lockAcquire) {
                    redisUtils.releaseLock(judgeLockKey);
                }
            }

            List<String> questionIdsList = Arrays.asList(questionIdsArray);
            QueryWrapper<content> contentsQueryWrapper = new QueryWrapper<>();
            contentsQueryWrapper.in("question_id", questionIdsList);
            questionContents = contentEntityService.list(contentsQueryWrapper);
            redisUtils.set(questionContentsCache, questionContents);
        }


        Map<String, content> questionContentMap = questionContents.stream().collect(
                Collectors.toMap(content::getQuestionId, Function.identity()));

        for (String questionId : questionIdsArray) {

            content questionContent = questionContentMap.get(questionId);
            if (questionContent != null) {
                String correctAnswer = questionContent.getRightAnswer();
                List<String> userSelectedOptions = examFinishDTO.getAnswersMap().get(questionId);
                if (userSelectedOptions != null && !userSelectedOptions.isEmpty()) {
                    String selectedOptions = String.join(",", userSelectedOptions);

                    if (correctAnswer.equals(selectedOptions) && questionContent.getQuestionType() == 1) {
                        totalScore += radioScore;
                    }
                    if (correctAnswer.equals(selectedOptions) && questionContent.getQuestionType() == 2) {
                        totalScore += checkScore;
                    }
                    if (correctAnswer.equals(selectedOptions) && questionContent.getQuestionType() == 3) {
                        totalScore += judgeScore;
                    }
                    if (correctAnswer.equals(selectedOptions) && questionContent.getQuestionType() == 4) {
                        totalScore += fillScore;
                    }
                    if (answerOptionIdsBuilder.length() > 0) {
                        answerOptionIdsBuilder.append("_");
                    }
                    answerOptionIdsBuilder.append(questionId).append("-").append(String.join("-", userSelectedOptions));
                }
            }
        }
        LocalDateTime examStartDate = exam.getExamStartDate();
        LocalDateTime examEndDate = exam.getExamEndDate();

        String examProblemIds = exam.getProblemId();

        if (examProblemIds != null) {
            CompletableFuture<Integer> integerCompletableFuture = examAsyncManager.judgeExamCode(examQuestionIds, examStartDate, examEndDate, userRolesVo.getUid(), codeScore);
            totalScore += integerCompletableFuture.join();
        }


        examRecord examRecord = new examRecord();
        examRecord.setExamRecordId(examId + "-" + userRolesVo.getUid());
        examRecord.setExamId(examId);
        examRecord.setExamJoinerId(userRolesVo.getUid());
        examRecord.setExamJoinDate(examJoinDate);
        examRecord.setExamTimeCost(examTimeCost);
        examRecord.setExamJoinScore(totalScore);
        examRecord.setAnswerOptionIds(answerOptionIdsBuilder.toString());
        examRecordEntityService.save(examRecord);
        String recordCacheKey = "examRecordDetail:" + examId + "-" + userRolesVo.getUid();
        redisUtils.del(recordCacheKey);
        redisUtils.set(recordCacheKey, examRecord);

        return examRecord;
    }

    public examRecordDetailVO getExamRecordDetail(String examId) throws StatusNotFoundException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        String recordId = examId + "-" + userRolesVo.getUid();
//        String recordId = examId + "-" + "xjl666";
        examRecordDetailVO examRecordDetailVO = new examRecordDetailVO();
        examRecord record;
        QueryWrapper<examRecord> examRecordQueryWrapper = new QueryWrapper<>();
        examRecordQueryWrapper.eq("exam_record_id", recordId);
        record = examRecordEntityService.getOne(examRecordQueryWrapper);

        if (record == null) {
            throw new StatusNotFoundException("获取失败，该考试记录不存在！");
        }

        examRecordDetailVO.setExamRecord(record);

        HashMap<String, List<String>> answersMap = new HashMap<>();
        String answerOptionIds = record.getAnswerOptionIds();
        if (answerOptionIds != null && !answerOptionIds.isEmpty()) {
            String[] questionAnswers = answerOptionIds.split("_");
            for (String questionAnswer : questionAnswers) {
                String[] parts = questionAnswer.split("-");
                String questionId = parts[0];
                List<String> selectedOptions = Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length));
                answersMap.put(questionId, selectedOptions);
            }
        }
        examRecordDetailVO.setAnswersMap(answersMap);

        HashMap<String, String> resultsMap = new HashMap<>();
        HashMap<String, List<String>> answersRightMap = new HashMap<>();

        for (String questionId : answersMap.keySet()) {
            List<String> userSelectedOptions = answersMap.get(questionId);
            QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
            contentQueryWrapper.eq("question_id", questionId);
            content content = contentEntityService.getOne(contentQueryWrapper);
            if (content == null) {
                continue;
            }
            String correctAnswer = content.getRightAnswer();

            List<String> userSelectedSorted = new ArrayList<>(userSelectedOptions);
            Collections.sort(userSelectedSorted);
            List<String> correctAnswerSorted = Arrays.asList(correctAnswer.split(","));
            Collections.sort(correctAnswerSorted);

            Boolean isRight = userSelectedSorted.equals(correctAnswerSorted);
            resultsMap.put(questionId, isRight ? "True" : "False");
            answersRightMap.put(questionId, correctAnswerSorted);
        }

        String examKey = "exam:" + examId;
        exam cachedExam = (exam) redisUtils.get(examKey);

        exam exam;
        if (cachedExam == null) {
            QueryWrapper<exam> examQueryWrapper1 = new QueryWrapper<>();
            examQueryWrapper1
                    .eq("exam_id", examId);
            exam = examEntityService.getOne(examQueryWrapper1);
            if (exam == null) {
                throw new StatusNotFoundException("获取失败，该试卷不存在！");
            }
            redisUtils.set(examKey, exam);
        } else {
            exam = cachedExam;
        }
        examVO examVO = new examVO();
        examVO.setExam(exam);
        examVO.setSelectQuestionVOs(new ArrayList<>());

        String examQuestionIds = exam.getQuestionId();
        String[] questionIdsArray = examQuestionIds.split(",");

        String examProblemIds = examVO.getExam().getProblemId();
        if (examProblemIds!=null){
            String[] problemIdsArray = examProblemIds.split(",");
            List<String> problemIds = new ArrayList<>();
            for (String problemId : problemIdsArray) {
                problemIds.add(problemId);
            }
            examVO.setProblemId(problemIds);
        }
        for (String questionId : questionIdsArray) {
            questionContentVO questionContentVO = contentEntityService.getQuestionByQid(questionId);
            if (questionContentVO == null) {
                continue; // Handle case where question content is not found
            }
            examSelectQuestionVO selectQuestionVO = new examSelectQuestionVO();
            selectQuestionVO.setQuestionId(questionId);

            selectQuestionVO.setQuestionContentVOs(new ArrayList<>());
            selectQuestionVO.setOptions(new ArrayList<>());

            selectQuestionVO.getQuestionContentVOs().add(questionContentVO);

            if (1==questionContentVO.getQuestionType() || 2==questionContentVO.getQuestionType())  {
                QueryWrapper<options> optionsQueryWrapper = new QueryWrapper<>();
                optionsQueryWrapper.eq("question_id", questionId);
                List<options> optionsList = optionsEntityService.list(optionsQueryWrapper);
                selectQuestionVO.setOptions(optionsList);
            }
            examVO.getSelectQuestionVOs().add(selectQuestionVO);
        }
        examRecordDetailVO.setExamVO(examVO);
        examRecordDetailVO.setResultsMap(resultsMap);
        examRecordDetailVO.setAnswersRightMap(answersRightMap);


        return examRecordDetailVO;
    }


    public List<examListVO> examList() {
        List<examListVO> examListVOS = new ArrayList<>();
        List<exam> exams = examEntityService.list();
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        for (exam exam : exams) {
            examListVO examListVO = new examListVO();
            QueryWrapper<examRecord> examRecordQueryWrapper = new QueryWrapper<>();
            examRecordQueryWrapper
                    .eq("exam_id", exam.getExamId())
                    .eq("exam_joiner_id", userRolesVo.getUid());

            List<examRecord> record = examRecordEntityService.list(examRecordQueryWrapper);
            if (!record.isEmpty()) {
                examListVO.setExam(exam);
                examListVO.setIsJoined(true);
            } else {
                examListVO.setExam(exam);
                examListVO.setIsJoined(false);
            }
            examListVOS.add(examListVO);
        }
        return examListVOS;
    }


    public String ExamSentPassword(examEmailDTO examEmailDTO) throws StatusFailException {
        String email = examEmailDTO.getEmail();
        String examId = examEmailDTO.getExamId();

        boolean isMatch = commonValidator.validateEmailFormat(email);
        if (!isMatch) {
            throw new StatusFailException("请输入正确的邮箱！");
        }
        String requestCountKey = "requestCount:" + email + "-" + examId;
        if (redisUtils.get(requestCountKey) != null) {
            throw new StatusFailException("请在1分钟后重新获取密码！");
        }
        int count = 0;
        String countStr = (String) redisUtils.get(requestCountKey);
        if (countStr != null) {
            count = Integer.parseInt(countStr);
        }
        int maxRequestsPerMinute = 1;
        if (count >= maxRequestsPerMinute) {
            throw new StatusFailException("请求过于频繁，请稍后再试。");
        }
        count++;
        redisUtils.set(requestCountKey, String.valueOf(count), 60);
        rabbitTemplate.convertAndSend("examPasswordEmail", examEmailDTO);
        return "ok";
    }

    public List<HashMap<String, exam>> getExamNotice() {
        List<HashMap<String, exam>> list = new ArrayList<>();

        String examAddNoticeKey = "examAddNotice";
        String examUpdateNoticeKey = "examUpdateNotice";

        exam addExam = (exam) redisUtils.get(examAddNoticeKey);
        if (addExam != null) {
            HashMap<String, exam> map = new HashMap<>();
            map.put("add", addExam);
            list.add(map);
        }

        exam updateExam = (exam) redisUtils.get(examUpdateNoticeKey);
        if (updateExam != null) {
            HashMap<String, exam> map = new HashMap<>();
            map.put("update", updateExam);
            list.add(map);
        }
        return list;
    }

    public List examGetProblemId(String examId) {
        QueryWrapper<exam> examQueryWrapper = new QueryWrapper<>();
        examQueryWrapper.select("problem_id")
                .eq("exam_id", examId);
        exam exam = examEntityService.getOne(examQueryWrapper);
        String problemId = exam.getProblemId();
        List problemIds = new ArrayList<>();
        String[] problemIdArray = problemId.split(",");
        for (String id : problemIdArray) {
            problemIds.add(id.trim());
        }
        return problemIds;
    }
}
