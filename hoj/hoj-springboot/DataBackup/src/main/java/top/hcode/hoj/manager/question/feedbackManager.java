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
import top.hcode.hoj.dao.question.feedbackEntityService;
import top.hcode.hoj.mapper.questionContentMapper;
import top.hcode.hoj.pojo.entity.question.content;
import top.hcode.hoj.pojo.entity.question.feedback;
import top.hcode.hoj.shiro.AccountProfile;

@Component
public class feedbackManager {
    @Autowired
    feedbackEntityService feedbackEntityService;
    @Autowired
    contentEntityService contentEntityService;
    @Autowired
    questionContentMapper questionContentMapper;

    public feedback getFeedbackByQid(String questionId) throws StatusNotFoundException {
        QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper
                .eq("question_id", questionId);

        content content = contentEntityService.getOne(contentQueryWrapper);
        if (content == null) {
            throw new StatusNotFoundException("反馈失败，该题目已不存在！");
        }


        feedback feedback1 = feedbackEntityService.getFeedbackByQid(questionId);
        if (feedback1 == null) {
            throw new StatusNotFoundException("查询失败，该反馈已不存在！");
        }

        return feedback1;
    }

    public void addFeedback(feedback feedback) throws StatusForbiddenException, StatusFailException, StatusNotFoundException {
        String qid = feedback.getQuestionId();
        if (qid == null) {
            throw new StatusNotFoundException("!!!!");
        }

        QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper
                .eq("question_id", feedback.getQuestionId());

        content content = contentEntityService.getOne(contentQueryWrapper);
        if (content == null) {
            throw new StatusNotFoundException("反馈失败，该题目已不存在！");
        }

//        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
//
//        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
//        if (!isRoot
//                && !feedback.getUserId().equals(userRolesVo.getUid())) {
//            throw new StatusForbiddenException("对不起，您无权限操作！");
//        }

        boolean isOk = feedbackEntityService.saveOrUpdate(feedback);
        if (!isOk) {
            throw new StatusFailException("反馈失败，请重新尝试！");
        }

    }

    public void removeFeedbackById(int id) throws StatusForbiddenException, StatusFailException, StatusNotFoundException {


        QueryWrapper<feedback> feedbackQueryWrapper = new QueryWrapper<>();
        feedbackQueryWrapper
                .eq("id", id);

        feedback feedback1 = feedbackEntityService.getOne(feedbackQueryWrapper);
        if (feedback1 == null) {
            throw new StatusNotFoundException("删除反馈失败，该反馈已不存在！");
        }


        feedback feedback = feedbackEntityService.getOne(feedbackQueryWrapper);
        QueryWrapper<content> contentQueryWrapper1 = new QueryWrapper<>();
        contentQueryWrapper1
                .eq("question_id", feedback.getQuestionId());

        content content = contentEntityService.getOne(contentQueryWrapper1);
        if (content == null) {
            throw new StatusNotFoundException("删除反馈失败，该题目已不存在！");
        }

//        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
//
//        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
//        if (!isRoot
//                && !feedback.getUserId().equals(userRolesVo.getUid())) {
//            throw new StatusForbiddenException("对不起，您无权限操作！");
//        }

        boolean isOk = feedbackEntityService.removeById(id);
        if (!isOk) {
            throw new StatusFailException("删除失败，请重新尝试！");
        }

    }

    public void updateFeedbackById(feedback feedback) throws StatusForbiddenException, StatusFailException, StatusNotFoundException {
        QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper
                .eq("question_id", feedback.getQuestionId());

        content content = contentEntityService.getOne(contentQueryWrapper);
        if (content == null) {
            throw new StatusNotFoundException("更新反馈失败，该题目已不存在！");
        }

        QueryWrapper<feedback> feedbackQueryWrapper1 = new QueryWrapper<>();
        feedbackQueryWrapper1
                .eq("id", feedback.getId());

        feedback feedback2 = feedbackEntityService.getOne(feedbackQueryWrapper1);
        if (feedback2 == null) {
            throw new StatusNotFoundException("更新反馈失败，原反馈已不存在！");
        }

//        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
//
//        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
//        if (!isRoot
//                && !feedback.getUserId().equals(userRolesVo.getUid())) {
//            throw new StatusForbiddenException("对不起，您无权限操作！");
//        }

        UpdateWrapper<feedback> feedbackUpdateWrapper = new UpdateWrapper<>();
        feedbackUpdateWrapper.set("question_id", feedback.getQuestionId())
                .set("user_id", feedback.getUserId())
                .set("question_type", feedback.getQuestionType())
                .set("judge_result", feedback.getJudgeResult())
                .set("question_score", feedback.getQuestionScore())
                .set("judge_time", feedback.getJudgeTime())
                .set("right_answer", feedback.getRightAnswer())
                .set("user_answer", feedback.getUserAnswer())
                .eq("id", feedback.getId());

        boolean isOk = feedbackEntityService.update(feedbackUpdateWrapper);
        if (!isOk) {
            throw new StatusFailException("更新反馈失败，请重新尝试！");
        }
    }
}
