package top.hcode.hoj.service.question.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusForbiddenException;
import top.hcode.hoj.common.exception.StatusNotFoundException;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.common.result.ResultStatus;
import top.hcode.hoj.manager.question.feedbackManager;
import top.hcode.hoj.pojo.entity.question.feedback;
import top.hcode.hoj.service.question.feedbackService;

@Service
public class feedbackServiceImpl implements feedbackService {
    @Autowired
    feedbackManager feedbackManager;
    @Override
    public CommonResult<feedback> getFeedbackByQid(String questionId) {
        try{
            return CommonResult.successResponse(feedbackManager.getFeedbackByQid(questionId));
        }catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }
    }

    @Override
    public CommonResult<Void> addFeedback(feedback feedback) {
        try{
            feedbackManager.addFeedback(feedback);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }

    }

    @Override
    public CommonResult<Void> removeFeedbackById(int id) {
        try{
            feedbackManager.removeFeedbackById(id);
            return CommonResult.successResponse();
        }catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }
    }

    @Override
    public CommonResult<Void> updateFeedbackById(feedback feedback) {
        try{
            feedbackManager.updateFeedbackById(feedback);
            return CommonResult.successResponse();
        }catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }
    }


}
