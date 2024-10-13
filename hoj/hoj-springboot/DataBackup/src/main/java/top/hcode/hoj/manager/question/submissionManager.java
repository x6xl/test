package top.hcode.hoj.manager.question;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusForbiddenException;
import top.hcode.hoj.common.exception.StatusNotFoundException;
import top.hcode.hoj.dao.question.contentEntityService;
import top.hcode.hoj.dao.question.submissionEntityService;
import top.hcode.hoj.pojo.entity.question.content;
import top.hcode.hoj.pojo.entity.question.submission;
import top.hcode.hoj.shiro.AccountProfile;
import top.hcode.hoj.utils.RedisUtils;

@Component
public class submissionManager {
    @Autowired
    submissionEntityService submissionEntityService;
    @Autowired
    contentEntityService contentEntityService;

    @Autowired
    RedisUtils redisUtils;

    public submission getSubmissionByQid(String questionId) throws StatusNotFoundException {
        QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper
                .eq("question_id", questionId);

        content content = contentEntityService.getOne(contentQueryWrapper);
        if (content == null) {
            throw new StatusNotFoundException("查询失败，该题目已不存在！");
        }


        submission submission1 = submissionEntityService.getSubmissionByQid(questionId);
        if (submission1 == null) {
            throw new StatusNotFoundException("查询失败，没有查到改题的提交记录");
        }

        return submission1;
    }


    public void addSubminssion(submission submission) throws StatusForbiddenException, StatusFailException, StatusNotFoundException {
        QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper
                .eq("question_id", submission.getQuestionId());

        content content = contentEntityService.getOne(contentQueryWrapper);
        if (content == null) {
            throw new StatusNotFoundException("提交失败，该题目已不存在！");
        }

        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        if (userRolesVo == null) {
            throw new StatusForbiddenException("还未登入，请先登入在提交！");
        }
//        String key=submission.getUserId()+"_"+submission.getQuestionId();
//
//        QueryWrapper<submission> submissionQueryWrapper1 = new QueryWrapper<>();
//        submissionQueryWrapper1
//                .eq("question_id", submission.getQuestionId());
//        submission submission1= submissionEntityService.getOne(submissionQueryWrapper1);
//        if(submission1==null){
        submission.setUserId(userRolesVo.getUid());
        submission.setSubmitTime(submission.getSubmitTime());
        boolean isOk = submissionEntityService.saveOrUpdate(submission);
        if (!isOk) {
            throw new StatusFailException("提交失败，请重新尝试！");
        }

//            redisUtils.set(key, 1, 10);//设置10s
//        }
//        Integer trySubmissionCount = (Integer) redisUtils.get(key);
//        if (trySubmissionCount!=0){
//        throw new StatusFailException("提交次数频繁，请在提交后10s才能再次提交！");
//        }
    }


    public void updateSubminssion(submission submission) throws StatusForbiddenException, StatusFailException, StatusNotFoundException {
        QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper
                .eq("question_id", submission.getQuestionId());

        content content = contentEntityService.getOne(contentQueryWrapper);
        if (content == null) {
            throw new StatusNotFoundException("更新提交失败，该题目已不存在！");
        }

        QueryWrapper<submission> submissionQueryWrapper1 = new QueryWrapper<>();
        submissionQueryWrapper1
                .eq("id", submission.getId());
        submission submission2 = submissionEntityService.getOne(submissionQueryWrapper1);
        if (submission2 == null) {
            throw new StatusNotFoundException("查询失败，该提交已不存在！");
        }

//        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
//
//        if (userRolesVo == null) {
//            throw new StatusForbiddenException("还未登入，请先登入在提交！");
//        }

        UpdateWrapper<submission> submissionUpdateWrapper = new UpdateWrapper<>();
        submissionUpdateWrapper.set("question_id", submission.getQuestionId())
                .set("user_id", submission.getUserId())
                .set("question_type", submission.getQuestionType())
                .set("submit_time", submission.getSubmitTime())
                .set("submit_result", submission.getSubmitResult())
                .eq("id", submission.getId());

        boolean isOk = submissionEntityService.update(submissionUpdateWrapper);
        if (!isOk) {
            throw new StatusFailException("更新提交失败，请重新尝试！");
        }

    }
}
