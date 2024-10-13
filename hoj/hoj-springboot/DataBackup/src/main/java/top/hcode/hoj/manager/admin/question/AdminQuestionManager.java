package top.hcode.hoj.manager.admin.question;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusForbiddenException;
import top.hcode.hoj.common.exception.StatusNotFoundException;
import top.hcode.hoj.dao.question.*;
import top.hcode.hoj.pojo.dto.ExamAddProblemDTO;
import top.hcode.hoj.pojo.dto.QuestionDTO;
import top.hcode.hoj.pojo.dto.examCreateDTO;
import top.hcode.hoj.pojo.dto.questionIsSelectDTO;
import top.hcode.hoj.pojo.entity.question.*;
import top.hcode.hoj.pojo.vo.*;
import top.hcode.hoj.repository.contentRepository;
import top.hcode.hoj.shiro.AccountProfile;
import top.hcode.hoj.utils.RedisUtils;
import top.hcode.hoj.validator.questionValidator;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RefreshScope
@Slf4j(topic = "hoj")
public class AdminQuestionManager {

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

    @Resource
    questionValidator questionValidator;

    @Autowired
    private RedisUtils redisUtils;

    @Resource
    contentRepository contentRepository;


    public List<content> getQuestionList() {

        List<content> list = contentEntityService.list();
        contentRepository.saveAll(list);
        return list;
    }

    public org.springframework.data.domain.Page<content> getQuestionListPage1(Integer limit, Integer currentPage, String keyword, int questionType) {

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;

        Pageable pageable = PageRequest.of(currentPage - 1, limit);

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (questionType > 0) {
            boolQuery.must(QueryBuilders.termQuery("questionType", questionType));
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            String trimmedKeyword = keyword.trim();
            boolQuery.must(QueryBuilders.multiMatchQuery(trimmedKeyword, "questionId", "author", "questionContent"));
        }

        // 执行查询并分页
        return contentRepository.search(boolQuery, pageable);
    }


    public IPage<content> getQuestionListPage(Integer limit, Integer currentPage, String keyword, int questionType) {
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 2;
        IPage<content> iPage = new Page<>(currentPage, limit);
        IPage<content> questionList;

        QueryWrapper<content> queryWrapper = new QueryWrapper<>();
        if (questionType > 0) {
            queryWrapper.eq("question_type", questionType);
        }

        if (!StringUtils.isEmpty(keyword)) {
            final String key = keyword.trim();
            queryWrapper.and(wrapper -> wrapper.like("question_id", key).or()
                    .like("author", key).or()
                    .like("question_content", key));
            questionList = contentEntityService.page(iPage, queryWrapper);
        } else {
            questionList = contentEntityService.page(iPage, queryWrapper);
        }
        return questionList;
    }

    public content getQuestionByQid(String questionId) throws StatusForbiddenException, StatusFailException {
        QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper
                .eq("question_id", questionId);
        content content = contentEntityService.getOne(contentQueryWrapper);
        if (content != null) { // 查询成功
            // 获取当前登录的用户
            AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

            boolean isRoot = SecurityUtils.getSubject().hasRole("root");
            boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
            // 只有超级管理员和题目管理员、题目创建者才能操作
            if (!isRoot && !isProblemAdmin && !userRolesVo.getUsername().equals(content.getAuthor())) {
                throw new StatusForbiddenException("对不起，你无权限查看题目！");
            }
            return content;
        } else {
            throw new StatusFailException("查询失败！666");
        }
    }

    public void deleteQuestion(String questionId) throws StatusForbiddenException, StatusFailException {
        QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper
                .eq("question_id", questionId);
        boolean isOk = contentEntityService.remove(contentQueryWrapper);
        content content = contentEntityService.getOne(contentQueryWrapper);
        // 获取当前登录的用户
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        // 只有超级管理员和题目管理员、题目创建者才能操作
        if (!isRoot && !isProblemAdmin && !userRolesVo.getUsername().equals(content.getAuthor())) {
            throw new StatusForbiddenException("对不起，你无权限查看题目！");
        }

        contentRepository.deleteByQuestionId(questionId);
        QueryWrapper<options> optionsQueryWrappe = new QueryWrapper<>();
        optionsQueryWrappe
                .eq("question_id", questionId);
        boolean isOk1 = optionsEntityService.remove(optionsQueryWrappe);
        if (isOk && isOk1) { // 删除成功

        } else {
            throw new StatusFailException("删除失败！");
        }

    }

    @RabbitListener(queuesToDeclare = @Queue("questionImportFile"))
    public void processMessage(QuestionDTO questionDTO) throws StatusForbiddenException, StatusFailException {
        try {
            addQuestion(questionDTO);
        } catch (StatusFailException e) {
            e.printStackTrace();
        }
    }


    @RabbitHandler
    public void addQuestion(QuestionDTO questionDTO) throws StatusForbiddenException, StatusFailException {
        questionValidator.validateProblem(questionDTO.getContent());
        for (options option : questionDTO.getOptions()) {
            questionValidator.validateoptions(option);
        }

        QueryWrapper<content> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_id", questionDTO.getContent().getQuestionId().toUpperCase());
        content content = contentEntityService.getOne(queryWrapper);
        if (content != null) {
            throw new StatusFailException("该题目的Question ID已存在，请更换！");
        }
        // 获取当前登录的用户
        System.out.println(SecurityUtils.getSubject().getPrincipal());
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        // 只有超级管理员和题目管理员、题目创建者才能操作
        if (!isRoot && !isProblemAdmin && !userRolesVo.getUsername().equals(content.getAuthor())) {
            throw new StatusForbiddenException("对不起，你无权限查看题目！");
        }
        questionDTO.getContent().setAuthor(userRolesVo.getUsername());
        questionDTO.getContent().setAuthorId(userRolesVo.getUid());


        boolean isOk1 = contentEntityService.saveOrUpdate(questionDTO.getContent());
        contentRepository.save(questionDTO.getContent());
        boolean isOk2 = true;
        for (options option : questionDTO.getOptions()) {
            option.setAuthor(userRolesVo.getUsername());
            option.setQuestionId(questionDTO.getContent().getQuestionId()); // 确保每个选项都有正确的questionId
            isOk2 = optionsEntityService.saveOrUpdate(option);
        }
        if (!isOk1 || !isOk2) {
            throw new StatusFailException("添加题目失败");
        }
    }

    @RabbitListener(queuesToDeclare = @Queue("questionContentsAdd"))
    public void addQuestions(List<QuestionDTO> questionDTOs) throws StatusForbiddenException, StatusFailException {
        for (QuestionDTO questionDTO : questionDTOs) {
            addQuestion(questionDTO);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateQuestion(QuestionDTO questionDTO) throws StatusFailException, StatusForbiddenException {
        questionValidator.validateProblemUpdate(questionDTO.getContent());
        for (options option : questionDTO.getOptions()) {
            questionValidator.validateoptions(option);
        }


        // 获取当前登录的用户
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        // 只有超级管理员和题目管理员、题目创建者才能操作
        if (!isRoot && !isProblemAdmin && !userRolesVo.getUsername().equals(questionDTO.getContent().getAuthor())) {
            throw new StatusForbiddenException("对不起，你无权限修改题目！");
        }
        String questionId = questionDTO.getContent().getQuestionId().toUpperCase();
        QueryWrapper<content> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_id", questionId);
        content content = contentEntityService.getOne(queryWrapper);
        if (content == null) {
            throw new StatusFailException("修改失败，该题已经不存在！");
        }

        QueryWrapper<options> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("question_id", questionId);

        // 如果problem_id不是原来的且已存在该problem_id，则修改失败！
//        if ( content!= null && content.getQuestionId() != questionDTO.getContent().getQuestionId()) {
//            throw new StatusFailException("当前的Problem ID 已被使用，请重新更换新的！");
//        }

        // 记录修改题目的用户
//        questionDTO.getContent().setAuthor(userRolesVo.getUsername());

        UpdateWrapper<content> contentUpdateWrapper = new UpdateWrapper<>();
        contentUpdateWrapper.set("author", userRolesVo.getUid())
                .set("question_type", questionDTO.getContent().getQuestionType())
                .set("question_score", questionDTO.getContent().getQuestionScore())
                .set("question_content", questionDTO.getContent().getQuestionContent())
                .set("right_answer", questionDTO.getContent().getRightAnswer())
                .set("create_time", questionDTO.getContent().getCreateTime())
                .set("author_id", userRolesVo.getUid())
                .eq("question_id", questionDTO.getContent().getQuestionId());

        boolean result1 = contentEntityService.update(contentUpdateWrapper);

        content content1 = new content();
        content1.setId(content.getId());
        content1.setQuestionId(questionDTO.getContent().getQuestionId());
        content1.setAuthor(questionDTO.getContent().getAuthor());
        content1.setQuestionContent(questionDTO.getContent().getQuestionContent());
        content1.setQuestionType(questionDTO.getContent().getQuestionType());
        content1.setRightAnswer(questionDTO.getContent().getRightAnswer());
        content1.setAuthorId(questionDTO.getContent().getAuthorId());
        content1.setCreateTime(questionDTO.getContent().getCreateTime());
        content1.setQuestionScore(questionDTO.getContent().getQuestionScore());
        contentRepository.save(content1);


        boolean result2 = true;
        for (options option : questionDTO.getOptions()) {
            UpdateWrapper<options> optionsUpdateWrapper = new UpdateWrapper<>();
            optionsUpdateWrapper
                    .eq("question_id", option.getQuestionId())
                    .eq("id", option.getId());
            result2 = optionsEntityService.saveOrUpdate(option, optionsUpdateWrapper);
        }

        if (result1 && result2) { // 更新成功
//            if (content == null) { // 说明改了problemId，同步一下judge表
//                UpdateWrapper<Judge> judgeUpdateWrapper = new UpdateWrapper<>();
//                judgeUpdateWrapper.eq("pid", problemDto.getProblem().getId())
//                        .set("display_pid", problemId);
//                judgeEntityService.update(judgeUpdateWrapper);
//            }
        } else {
            throw new StatusFailException("修改失败");
        }

    }

    public List<options> getOptionsByQid(String questionId) {
        QueryWrapper<options> optionsQueryWrapper = new QueryWrapper<>();
        optionsQueryWrapper
                .eq("question_id", questionId);
        return optionsEntityService.list(optionsQueryWrapper);
    }

    public List<content> getQuestionListFind(String keyword) {
        List<content> questionList;
        QueryWrapper<content> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(keyword)) {
            final String key = keyword.trim();
            queryWrapper.and(wrapper -> wrapper.like("question_id", key).or()
                    .like("author", key).or()
                    .like("question_content", key));
            questionList = contentEntityService.list(queryWrapper);
        } else {
            questionList = contentEntityService.list(queryWrapper);
        }
        return questionList;
    }

    public void createExam(examCreateDTO examCreateDTO) throws StatusForbiddenException {
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        // 只有超级管理员和题目管理员、题目创建者才能操作
        if (!isRoot) {
            throw new StatusForbiddenException("对不起，你无权创建考试！");
        }
        exam exam = new exam();
        BeanUtils.copyProperties(examCreateDTO, exam);
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        exam.setExamCreatorId(userRolesVo.getUid());
//        exam.setExamCreatorId("1");

        List<questionIsSelectDTO> questions = examCreateDTO.getQuestions();
        List<String> selectedQuestionIds = new ArrayList<>();

        int radioCnt = 0, checkCnt = 0, judgeCnt = 0, fillCnt = 0;
        for (questionIsSelectDTO question : questions) {
            if (question.getChecked()) {
                selectedQuestionIds.add(question.getQuestionId());
                QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
                contentQueryWrapper.select("question_type")
                        .eq("question_id", question.getQuestionId());

                content content = contentEntityService.getOne(contentQueryWrapper);
                if (content != null) {
                    int questionType = content.getQuestionType();
                    if (questionType == 1) {
                        radioCnt++;
                    } else if (questionType == 2) {
                        checkCnt++;
                    } else if (questionType == 3) {
                        judgeCnt++;
                    } else if (questionType == 4) {
                        fillCnt++;
                    }
                }
            }
        }
        exam.setQuestionId(String.join(",", selectedQuestionIds));
        int examScore = radioCnt * examCreateDTO.getExamScoreRadio() + checkCnt * examCreateDTO.getExamScoreCheck() + judgeCnt * examCreateDTO.getExamScoreJudge() + fillCnt * examCreateDTO.getExamScoreFill();
        exam.setExamScore(examScore);
        examEntityService.saveOrUpdate(exam);

        String cacheKey = "exam:" + examCreateDTO.getExamId();
        redisUtils.set(cacheKey, exam);

        examPassword examPassword = new examPassword();
        examPassword.setExamId(examCreateDTO.getExamId());
        examPassword.setPassword(examCreateDTO.getPassword());
        examPasswordEntityService.save(examPassword);
        String recordKey = "examPassword:" + exam.getExamId();
        redisUtils.set(recordKey, examPassword.getPassword());

        String examNoticeKey = "examAddNotice";
        redisUtils.set(examNoticeKey, exam, exam.getExamTimeLimit() * 60);
    }

    public void updateExam(examCreateDTO examCreateDTO) throws StatusForbiddenException, StatusFailException {
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        // 只有超级管理员和题目管理员、题目创建者才能操作
        if (!isRoot) {
            throw new StatusForbiddenException("对不起，你无权限修改考试！");
        }

        QueryWrapper<exam> examQueryWrapper = new QueryWrapper<>();
        examQueryWrapper
                .eq("exam_id", examCreateDTO.getExamId());
        exam exam = examEntityService.getOne(examQueryWrapper);
        if (exam == null) {
            throw new StatusFailException("更新失败，该考试不存在");
        }


        exam.setExamName(examCreateDTO.getExamName());
        exam.setExamDescription(examCreateDTO.getExamDescription());
        exam.setExamTimeLimit(examCreateDTO.getExamTimeLimit());
        exam.setExamScoreCheck(examCreateDTO.getExamScoreCheck());
        exam.setExamScoreJudge(examCreateDTO.getExamScoreJudge());
        exam.setExamScoreRadio(examCreateDTO.getExamScoreRadio());
        exam.setExamScoreFill(examCreateDTO.getExamScoreFill());
        exam.setExamScoreCode(examCreateDTO.getExamScoreCode());
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        exam.setExamCreatorId(userRolesVo.getUid());
//        exam.setExamCreatorId("2");
        exam.setExamStartDate(examCreateDTO.getExamStartDate());
        exam.setExamEndDate(examCreateDTO.getExamEndDate());
        List<questionIsSelectDTO> questions = examCreateDTO.getQuestions();
        List<String> selectedQuestionIds = new ArrayList<>();

        int radioCnt = 0, checkCnt = 0, judgeCnt = 0, fillCnt = 0;
        for (questionIsSelectDTO question : questions) {
            if (question.getChecked()) {
                selectedQuestionIds.add(question.getQuestionId());
                QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
                contentQueryWrapper.select("question_type")
                        .eq("question_id", question.getQuestionId());

                content content = contentEntityService.getOne(contentQueryWrapper);
                if (content != null) {
                    int questionType = content.getQuestionType();
                    if (questionType == 1) {
                        radioCnt++;
                    } else if (questionType == 2) {
                        checkCnt++;
                    } else if (questionType == 3) {
                        judgeCnt++;
                    } else if (questionType == 4) {
                        fillCnt++;
                    }
                }
            }
        }
        exam.setQuestionId(String.join(",", selectedQuestionIds));
        int examScore = radioCnt * examCreateDTO.getExamScoreRadio() + checkCnt * examCreateDTO.getExamScoreCheck() + judgeCnt * examCreateDTO.getExamScoreJudge() + fillCnt * examCreateDTO.getExamScoreFill();
        exam.setExamScore(examScore);
        examEntityService.update(exam, examQueryWrapper);

        // 更新Redis缓存中的考试信息
        String cacheKey = "exam:" + examCreateDTO.getExamId();
        redisUtils.set(cacheKey, exam);

        QueryWrapper<examPassword> examPasswordQueryWrapper = new QueryWrapper<>();
        examPasswordQueryWrapper
                .eq("exam_id", examCreateDTO.getExamId());
        examPassword examPassword = examPasswordEntityService.getOne(examPasswordQueryWrapper);
        examPassword.setPassword(examCreateDTO.getPassword());
        examPasswordEntityService.update(examPassword, examPasswordQueryWrapper);
        String recordKey = "examPassword:" + exam.getExamId();
        redisUtils.set(recordKey, examPassword.getPassword());

        String examNoticeKey = "examUpdateNotice";
        redisUtils.set(examNoticeKey, exam, exam.getExamTimeLimit() * 60);
    }

    public void deleteExam(String examId) throws StatusForbiddenException, StatusFailException {
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        // 只有超级管理员和题目管理员、题目创建者才能操作
        if (!isRoot) {
            throw new StatusForbiddenException("对不起，你无权限删除题目！");
        }
        QueryWrapper<exam> examQueryWrapper = new QueryWrapper<>();
        examQueryWrapper
                .eq("exam_id", examId);
        exam exam = examEntityService.getOne(examQueryWrapper);
        if (exam == null) {
            throw new StatusFailException("删除失败!,考试已不存在，");
        }
        boolean isOk = examEntityService.remove(examQueryWrapper);
        QueryWrapper<examRecord> examRecordQueryWrapper = new QueryWrapper<>();
        examRecordQueryWrapper
                .eq("exam_id", examId);
        examRecordEntityService.remove(examRecordQueryWrapper);
        if (!isOk) {
            throw new StatusFailException("删除失败!");
        }
        QueryWrapper<examPassword> examPasswordQueryWrapper = new QueryWrapper<>();
        examPasswordQueryWrapper
                .eq("exam_id", examId);
        examPasswordEntityService.remove(examPasswordQueryWrapper);

        // 从Redis缓存中移除考试信息
        String cacheKey = "exam:" + examId;
        if (redisUtils.get(cacheKey) != null) {
            redisUtils.del(cacheKey);
        }
        String recordKey = "examPassword:" + exam.getExamId();
        if (redisUtils.get(recordKey) != null) {
            redisUtils.del(recordKey);
        }
        String questionContentsCache = "judgeExam"+examId;
        if (redisUtils.get(questionContentsCache)!=null){
            redisUtils.del(questionContentsCache);
        }

    }

    public List<exam> examList() {
        return examEntityService.list();
    }


    public adminExamVO getExam(String examId) throws StatusNotFoundException {
        String cacheKey = "exam:" + examId;
        exam cachedExam = (exam) redisUtils.get(cacheKey);

        exam exam;
        adminExamVO examVO = new adminExamVO();
        if (cachedExam == null) {
            // 如果缓存中没有，从数据库中获取
            QueryWrapper<exam> examQueryWrapper1 = new QueryWrapper<>();
            examQueryWrapper1.eq("exam_id", examId);
            exam = examEntityService.getOne(examQueryWrapper1);
            if (exam == null) {
                throw new StatusNotFoundException("获取失败，该试卷不存在");
            }

            examVO.setQuestionVOs(new ArrayList<>());
            examVO.setExam(exam);
            String examQuestionIds = examVO.getExam().getQuestionId();
            String[] questionIdsArray = examQuestionIds.split(",");

            String examProblemIds = examVO.getExam().getProblemId();
            String[] problemIdsArray = examProblemIds.split(",");
            List<String> problemIds = new ArrayList<>();
            for (String problemId : problemIdsArray) {
                problemIds.add(problemId);
            }
            examVO.setProblemId(problemIds);
            for (String questionId : questionIdsArray) {
                QueryWrapper queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("question_id", questionId);

                content content = contentEntityService.getOne(queryWrapper);
                examQuestionVO selectQuestionVO = new examQuestionVO();
                selectQuestionVO.setQuestionId(questionId);

                selectQuestionVO.setContents(new ArrayList<>());
                selectQuestionVO.setOptions(new ArrayList<>());

                selectQuestionVO.getContents().add(content);

                if (content.getQuestionType() == 1 || content.getQuestionType() == 2) {
                    QueryWrapper<options> optionsQueryWrapper = new QueryWrapper<>();
                    optionsQueryWrapper
                            .eq("question_id", questionId);
                    List<options> optionsList = optionsEntityService.list(optionsQueryWrapper);
                    selectQuestionVO.setOptions(optionsList);
                    String recordKey = "examPassword:" + exam.getExamId();
                    String password = (String) redisUtils.get(recordKey);
                    if (password == null) {
                        QueryWrapper<examPassword> examPasswordQueryWrapper = new QueryWrapper<>();
                        examPasswordQueryWrapper
                                .eq("exam_id", examId);
                        examPassword examPassword = examPasswordEntityService.getOne(examPasswordQueryWrapper);
                        password = examPassword.getPassword();
                        redisUtils.set(recordKey, password);
                    }
                }
                examVO.getQuestionVOs().add(selectQuestionVO);
            }
            // 将从数据库中获取的考试信息存入Redis缓存
            redisUtils.set(cacheKey, exam);
        } else {
            exam = cachedExam;
            examVO.setQuestionVOs(new ArrayList<>());

            examVO.setExam(exam);
            String examQuestionIds = exam.getQuestionId();
            String[] questionIdsArray = examQuestionIds.split(",");
            String examProblemIds = examVO.getExam().getProblemId();
            if (examProblemIds != null) {
                String[] problemIdsArray = examProblemIds.split(",");
                List<String> problemIds = new ArrayList<>();
                for (String problemId : problemIdsArray) {
                    problemIds.add(problemId);
                }
                examVO.setProblemId(problemIds);
            }

            for (String questionId : questionIdsArray) {
                QueryWrapper queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("question_id", questionId);

                content content = contentEntityService.getOne(queryWrapper);
                examQuestionVO QuestionVO = new examQuestionVO();
                QuestionVO.setQuestionId(questionId);

                QuestionVO.setContents(new ArrayList<>());
                QuestionVO.setOptions(new ArrayList<>());

                QuestionVO.getContents().add(content);

                if (content.getQuestionType() == 1 || content.getQuestionType() == 2) {
                    QueryWrapper<options> optionsQueryWrapper = new QueryWrapper<>();
                    optionsQueryWrapper
                            .eq("question_id", questionId);
                    List<options> optionsList = optionsEntityService.list(optionsQueryWrapper);
                    QuestionVO.setOptions(optionsList);
                    String recordKey = "examPassword:" + exam.getExamId();
                    String password = (String) redisUtils.get(recordKey);
                    if (password == null) {
                        QueryWrapper<examPassword> examPasswordQueryWrapper = new QueryWrapper<>();
                        examPasswordQueryWrapper
                                .eq("exam_id", examId);
                        examPassword examPassword = examPasswordEntityService.getOne(examPasswordQueryWrapper);
                        password = examPassword.getPassword();
                        redisUtils.set(recordKey, password);
                    }

                    examVO.setPassword(password);
                }
                examVO.getQuestionVOs().add(QuestionVO);
            }
        }
        return examVO;
    }

    public void deleteOption(int id) {
        QueryWrapper<options> optionsQueryWrapper = new QueryWrapper<>();
        optionsQueryWrapper.eq("id", id);
        optionsEntityService.remove(optionsQueryWrapper);
    }

    public void deleteQuestions(List<questionIsSelectDTO> questionIsSelectDTOs) throws StatusForbiddenException, StatusFailException {
        for (questionIsSelectDTO questionIsSelectDTO : questionIsSelectDTOs) {
            if (questionIsSelectDTO.getChecked() == true) {
                deleteQuestion(questionIsSelectDTO.getQuestionId());
            }
        }
    }

    public List<examRecord> examRecordList(String examId, int sort,String keyword) {
        QueryWrapper<examRecord> queryWrapper=new QueryWrapper<>();
        if (sort==0){
            queryWrapper.eq("exam_id",examId)
                    .like("exam_joiner_id",keyword)
                    .orderByDesc("exam_join_score");
        }else {
            queryWrapper.eq("exam_id",examId)
                    .like("exam_joiner_id",keyword)
                    .orderByAsc("exam_join_score");
        }
        List<examRecord> rank=examRecordEntityService.list(queryWrapper);
        return rank;
    }

    public void addFillQuestion(content content) throws StatusForbiddenException, StatusFailException {
        questionValidator.validateProblem(content);
        QueryWrapper<content> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_id", content.getQuestionId().toUpperCase());
        content content1 = contentEntityService.getOne(queryWrapper);
        if (content1 != null) {
            throw new StatusFailException("该题目的Question ID已存在，请更换！");
        }
        // 获取当前登录的用户
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        if (!isRoot && !isProblemAdmin) {
            throw new StatusForbiddenException("对不起，你无权限查看题目！");
        }
        content.setAuthorId(userRolesVo.getUid());
        content.setAuthor(userRolesVo.getUsername());
        boolean isOk = contentEntityService.saveOrUpdate(content);
        contentRepository.save(content);
        if (!isOk) {
            throw new StatusFailException("添加题目失败！");
        }
    }

    public void updateFillQuestion(content content) throws StatusFailException, StatusForbiddenException {
        questionValidator.validateProblemUpdate(content);

        // 获取当前登录的用户
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        // 只有超级管理员和题目管理员、题目创建者才能操作
        if (!isRoot && !isProblemAdmin) {
            throw new StatusForbiddenException("对不起，你无权限修改题目！");
        }
        String questionId = content.getQuestionId().toUpperCase();
        QueryWrapper<content> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_id", questionId);
        content content1 = contentEntityService.getOne(queryWrapper);
        if (content1 == null) {
            throw new StatusFailException("修改失败，该题已经不存在！");
        }


        // 记录修改题目的用户
//        questionDTO.getContent().setAuthor(userRolesVo.getUsername());

        UpdateWrapper<content> contentUpdateWrapper = new UpdateWrapper<>();
        contentUpdateWrapper.set("author", userRolesVo.getUsername())
                .set("question_type", content.getQuestionType())
                .set("question_score", content.getQuestionScore())
                .set("question_content", content.getQuestionContent())
                .set("right_answer", content.getRightAnswer())
                .set("create_time", content.getCreateTime())
                .set("author_id", userRolesVo.getUid())
                .eq("question_id", content.getQuestionId());
        boolean isOk = contentEntityService.update(contentUpdateWrapper);
        contentRepository.save(content);
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }

    public void deleteFillQuestion(String questionId) throws StatusFailException {
        QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper
                .eq("question_id", questionId);
        boolean isOk = contentEntityService.remove(contentQueryWrapper);
        contentRepository.deleteByQuestionId(questionId);
        if (!isOk) {
            throw new StatusFailException("删除失败");
        }
    }

    public void examAddProblem(ExamAddProblemDTO examAddProblemDTO) throws StatusFailException {
        UpdateWrapper<exam> examUpdateWrapper = new UpdateWrapper<>();
        examUpdateWrapper.set("problem_Id", String.join(",", examAddProblemDTO.getProblemId()))
                .eq("exam_id", examAddProblemDTO.getExamId());
        boolean isOk = examEntityService.update(examUpdateWrapper);
        if (!isOk) {
            throw new StatusFailException("添加代码题失败！");
        }

        String cacheKey = "exam:" + examAddProblemDTO.getExamId();

        QueryWrapper<exam> examQueryWrapper = new QueryWrapper<>();
        examQueryWrapper.eq("exam_id", examAddProblemDTO.getExamId());
        exam updatedExam = examEntityService.getOne(examQueryWrapper);

        if (updatedExam != null) {
            redisUtils.set(cacheKey, updatedExam);
        }
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

    public List<content> getQuestionList1() {
        Iterable<content> all = contentRepository.findAll();
        List<content> contentList = StreamSupport.stream(all.spliterator(), false)
                .collect(Collectors.toList());
        return contentList;
    }

}
