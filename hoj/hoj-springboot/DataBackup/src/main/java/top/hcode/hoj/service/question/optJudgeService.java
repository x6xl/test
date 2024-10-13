package top.hcode.hoj.service.question;

import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.entity.question.feedback;
import top.hcode.hoj.pojo.entity.question.submission;

public interface optJudgeService {

    CommonResult<feedback> judgeOpt(String question_id);
}
