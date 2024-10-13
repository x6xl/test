package top.hcode.hoj.controller.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.entity.question.feedback;
import top.hcode.hoj.service.question.feedbackService;

@RestController
@RequestMapping("/api")
public class feedbackController {
    @Autowired
    feedbackService feedbackService;

    @GetMapping("/question/getFeedback")
    public CommonResult<feedback> getFeedbackByQid(@RequestParam("question_id")String question_id){
        return feedbackService.getFeedbackByQid(question_id);
    }

    @RequestMapping("/question/addFeedback")
    public CommonResult<Void> addFeedback(feedback feedback){
        return feedbackService.addFeedback(feedback);
    }

    @RequestMapping("/question/delFeedback")
    public CommonResult<Void> removeFeedbackById(@RequestParam("id")int id){
        return feedbackService.removeFeedbackById(id);
    }

    @RequestMapping("/question/updateFeedback")
    public CommonResult<Void> updateQuestionOptionsById(feedback feedback){
        return feedbackService.updateFeedbackById(feedback);
    }
}
