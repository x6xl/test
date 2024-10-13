package top.hcode.hoj.controller.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.entity.question.options;
import top.hcode.hoj.service.question.optionsService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class optionsController {
    @Autowired
    optionsService optionsService;

    @GetMapping("/question/getoptions")
    public CommonResult<List<options>> getOptionsByQid(@RequestParam("question_id")String questionId){
        return optionsService.getOptionsByQid(questionId);
    }

    @RequestMapping("/question/addoptions")
    public CommonResult<Void> addQuestionOptions(@RequestBody options options){
        return optionsService.addOptions(options);
    }

    @RequestMapping("/question/deloptions")
    public CommonResult<Void> removeQuestionOptionsById(@RequestParam("id")int id){
        return optionsService.removeOptionsById(id);
    }

    @RequestMapping("/question/updateoptions")
    public CommonResult<Void> updateQuestionOptionsById(@RequestBody options options){
        return optionsService.updateOptionsById(options);
    }
}
