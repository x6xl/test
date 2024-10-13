package top.hcode.hoj.service.question;

import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.entity.question.submission;

public interface submissionService {
    CommonResult<submission> getSubmissionByQid(String questionId);

    CommonResult<Void> addSubminssion(submission submission);

    CommonResult<Void> updateSubminssionById(submission submission);
}
