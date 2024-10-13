package top.hcode.hoj.service.question;

import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.entity.question.feedback;

public interface feedbackService {
    CommonResult<feedback> getFeedbackByQid(String questionId);

    CommonResult<Void> addFeedback(feedback feedback);

    CommonResult<Void> removeFeedbackById(int id);

    CommonResult<Void> updateFeedbackById(feedback feedback);
}
