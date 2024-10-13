package top.hcode.hoj.controller.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.entity.question.submission;
import top.hcode.hoj.service.question.submissionService;


@RestController
@RequestMapping("/api")
public class submissionController {
    @Autowired
    submissionService submissionService;

    @RequestMapping("/question/getSubmission")
    public CommonResult<submission> getSubmissionByQid(@RequestParam("question_id")String question_id){
        return submissionService.getSubmissionByQid(question_id);
    }

    @PostMapping("/question/addSubmission")
    public CommonResult<Void> addSubminssionOptions( @RequestBody submission submission){
        return submissionService.addSubminssion(submission);
    }

    @RequestMapping("/question/updateSubminssion")
    public CommonResult<Void> updateQuestionSubminssionById(submission submission){
        return submissionService.updateSubminssionById(submission);
    }
}
