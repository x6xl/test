package top.hcode.hoj.controller.oj;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.hcode.hoj.annotation.AnonApi;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.entity.problem.CodeTemplate;
import top.hcode.hoj.pojo.entity.problem.Language;
import top.hcode.hoj.pojo.entity.problem.Tag;
import top.hcode.hoj.pojo.entity.training.TrainingCategory;
import top.hcode.hoj.pojo.vo.CaptchaVO;
import top.hcode.hoj.pojo.vo.ProblemTagVO;
import top.hcode.hoj.service.oj.CommonService;

import java.util.Collection;
import java.util.List;

/**
 * @Author: Himit_ZH
 * @Date: 2020/12/12 23:25
 * @Description: 通用的请求控制处理类
 */
@RestController
@RequestMapping("/api")
public class CommonController {

    @Autowired
    private CommonService commonService;

//获取验证码
    @GetMapping("/captcha")
    @AnonApi
    public CommonResult<CaptchaVO> getCaptcha() {
        return commonService.getCaptcha();
    }

//训练类别
    @GetMapping("/get-training-category")
    @AnonApi
    public CommonResult<List<TrainingCategory>> getTrainingCategory() {
        return commonService.getTrainingCategory();
    }
//获取所有问题的标签
    @GetMapping("/get-all-problem-tags")
    @AnonApi
    public CommonResult<List<Tag>> getAllProblemTagsList(@RequestParam(value = "oj", defaultValue = "ME") String oj) {
        return commonService.getAllProblemTagsList(oj);
    }
//问题的标签和分类
    @GetMapping("/get-problem-tags-and-classification")
    @AnonApi
    public CommonResult<List<ProblemTagVO>> getProblemTagsAndClassification(@RequestParam(value = "oj", defaultValue = "ME") String oj) {
        return commonService.getProblemTagsAndClassification(oj);
    }
//获取问题的标签
    @GetMapping("/get-problem-tags")
    @AnonApi
    public CommonResult<Collection<Tag>> getProblemTags(Long pid) {
        return commonService.getProblemTags(pid);
    }

//获取编程语言
    @GetMapping("/languages")
    @AnonApi
    public CommonResult<List<Language>> getLanguages(@RequestParam(value = "pid", required = false) Long pid,
                                                     @RequestParam(value = "all", required = false) Boolean all) {
        return commonService.getLanguages(pid, all);
    }
//问题所支持的语言
    @GetMapping("/get-problem-languages")
    @AnonApi
    public CommonResult<Collection<Language>> getProblemLanguages(@RequestParam("pid") Long pid) {
        return commonService.getProblemLanguages(pid);
    }
//问题代码模板
    @GetMapping("/get-problem-code-template")
    @AnonApi
    public CommonResult<List<CodeTemplate>> getProblemCodeTemplate(@RequestParam("pid") Long pid) {
        return commonService.getProblemCodeTemplate(pid);
    }

}