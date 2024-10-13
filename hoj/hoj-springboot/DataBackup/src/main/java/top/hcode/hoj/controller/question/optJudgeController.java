package top.hcode.hoj.controller.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.entity.question.feedback;
import top.hcode.hoj.pojo.entity.question.submission;
import top.hcode.hoj.service.question.optJudgeService;

@RestController
@RequestMapping("/api")
public class optJudgeController {
    @Autowired
    optJudgeService judgeService;

    @RequestMapping("/question/judgeOpt")
    public CommonResult<feedback> judgeOpt(@RequestParam("question_id")String question_id){
        return judgeService.judgeOpt(question_id);
    }
}
