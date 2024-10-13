package top.hcode.hoj.service.question.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusNotFoundException;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.common.result.ResultStatus;
import top.hcode.hoj.manager.question.optJudgeManager;
import top.hcode.hoj.pojo.entity.question.feedback;
import top.hcode.hoj.pojo.entity.question.submission;
import top.hcode.hoj.service.question.optJudgeService;

@Service
public class optJudgeServiceImpl implements optJudgeService {

    @Autowired
    optJudgeManager judgeManager;
    @Override
    public CommonResult<feedback> judgeOpt(String question_id) {
        try{
            return CommonResult.successResponse(judgeManager.judgeOpt(question_id));
        }catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }
    }
}
