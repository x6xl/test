package top.hcode.hoj.manager.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusNotFoundException;
import top.hcode.hoj.mapper.questionContentMapper;
import top.hcode.hoj.mapper.questionSubmissionMapper;
import top.hcode.hoj.pojo.entity.question.content;
import top.hcode.hoj.pojo.entity.question.feedback;
import top.hcode.hoj.pojo.entity.question.submission;
import top.hcode.hoj.service.question.feedbackService;

@Component
public class optJudgeManager {
    @Autowired
    private contentManager contentManager;
    @Autowired
    private questionSubmissionMapper questionSubmissionMapper;
    @Autowired
    private feedbackService feedbackService;

    public feedback judgeOpt(String question_id) throws StatusFailException, StatusNotFoundException {
        submission submission = questionSubmissionMapper.getSubmissionByQid(question_id);

        String userAnswer = submission.getSubmitResult();
        content content = contentManager.getQuestionContentByQid(question_id);
        String rightAnswer = content.getRightAnswer();
        if (content == null) {
            throw new StatusFailException("该题目已不存在!");
        }
        if (rightAnswer == null) {
            throw new StatusFailException("没找到正确答案!");
        }

        boolean isRight = userAnswer.equals(rightAnswer);
        if (isRight) {
            feedback feedback = new feedback();
            feedback.setQuestionType(content.getQuestionType());
            feedback.setQuestionId(content.getQuestionId());
            feedback.setUserId(submission.getUserId());
            feedback.setQuestionScore(content.getQuestionScore());
            feedback.setJudgeTime(feedback.getJudgeTime());
            feedback.setRightAnswer(content.getRightAnswer());
            feedback.setUserAnswer(submission.getSubmitResult());
            feedback.setJudgeResult("回答正确");
            feedbackService.addFeedback(feedback);
            return feedback;
        } else {
            feedback feedback = new feedback();
            feedback.setQuestionType(content.getQuestionType());
            feedback.setQuestionId(content.getQuestionId());
            feedback.setUserId(submission.getUserId());
            feedback.setQuestionScore(0);
            feedback.setJudgeTime(feedback.getJudgeTime());
            feedback.setRightAnswer(content.getRightAnswer());
            feedback.setUserAnswer(submission.getSubmitResult());
            feedback.setJudgeResult("回答错误");
            feedbackService.addFeedback(feedback);
            return feedback;
        }
    }
}
