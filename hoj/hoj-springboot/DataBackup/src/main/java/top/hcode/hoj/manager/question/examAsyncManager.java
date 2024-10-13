package top.hcode.hoj.manager.question;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.dao.judge.JudgeEntityService;
import top.hcode.hoj.dao.question.contentEntityService;
import top.hcode.hoj.dao.question.examPasswordEntityService;
import top.hcode.hoj.dao.question.optionsEntityService;
import top.hcode.hoj.pojo.entity.judge.Judge;
import top.hcode.hoj.pojo.entity.question.examPassword;
import top.hcode.hoj.pojo.entity.question.options;
import top.hcode.hoj.pojo.vo.questionContentVO;
import top.hcode.hoj.utils.Constants;
import top.hcode.hoj.utils.RedisUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class examAsyncManager {

    @Resource
    contentEntityService contentEntityService;

    @Resource
    optionsEntityService optionsEntityService;

    @Resource
    JudgeEntityService judgeEntityService;

    @Resource
    RedisUtils redisUtils;

    @Autowired
    examPasswordEntityService examPasswordEntityService;


    @Async
    public CompletableFuture<questionContentVO> getQuestionContentById(String questionId) {
        questionContentVO questionContentVO = contentEntityService.getQuestionByQid(questionId);
        return CompletableFuture.completedFuture(questionContentVO);
    }

    @Async
    public CompletableFuture<List<options>> getOptionsByQuestionId(String questionId) {
        QueryWrapper<options> optionsQueryWrapper = new QueryWrapper<>();
        optionsQueryWrapper.eq("question_id", questionId);
        List<options> optionsList = optionsEntityService.list(optionsQueryWrapper);
        return CompletableFuture.completedFuture(optionsList);
    }

    @Async
    public CompletableFuture<Integer> judgeExamCode(String examProblemIds, LocalDateTime examStartDate, LocalDateTime examEndDate, String userId, int codeScore){
        int totalScore=0;
        String[] problemIdsArray = examProblemIds.split(",");

        List<String> problemIdsList = Arrays.asList(problemIdsArray);
        QueryWrapper<Judge> judgesQueryWrapper = new QueryWrapper<>();
        judgesQueryWrapper.in("pid", problemIdsList)
                .eq("uid", userId)
                .orderByDesc("submit_time");
        List<Judge> judgesList =judgeEntityService.list(judgesQueryWrapper);
        Map<Long, Judge> judgeMap = judgesList.stream().collect(
                Collectors.toMap(Judge::getPid, Function.identity()));

        for (String problemId : problemIdsArray) {
            Judge judge = judgeMap.get(problemId);
            if (judge != null) {
                LocalDateTime submitTime = judge.getSubmitTime().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                if (submitTime.isAfter(examStartDate) && submitTime.isBefore(examEndDate)) {
                    if (judge != null && judge.getStatus().equals(Constants.Judge.STATUS_ACCEPTED.getStatus())) {
                        totalScore += codeScore;
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(totalScore);
    }

    @Async
    public CompletableFuture<String> getExamPassword(String examId) throws StatusFailException {
        String recordKey = "examPassword:" + examId;
        String lockKey = "examPasswrd:lock"+examId;
        String password1 = (String) redisUtils.get(recordKey);
        if (password1 == null) {
            //获取锁访问数据库，防止缓存击穿
            boolean lockAcquire=redisUtils.getLock(lockKey,10);
            try{
                if (lockAcquire){
                    QueryWrapper<examPassword> examPasswordQueryWrapper = new QueryWrapper<>();
                    examPasswordQueryWrapper.eq("exam_id", examId);
                    examPassword examPassword = examPasswordEntityService.getOne(examPasswordQueryWrapper);
                    if (examPassword != null) {
                        password1 = examPassword.getPassword();
                        redisUtils.set(recordKey, password1);
                    } else {
                        throw new StatusFailException("未找到考试密码记录");
                    }
                }else {
                    Thread.sleep(1000);
                    password1=(String) redisUtils.get(recordKey);
                    if (password1==null){
                        throw new StatusFailException("未找到考试密码记录");
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return CompletableFuture.completedFuture(password1);
    }

}
