package top.hcode.hoj.controller.question;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import top.hcode.hoj.annotation.AnonApi;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.entity.question.content;
import top.hcode.hoj.pojo.vo.questionContentVO;
import top.hcode.hoj.service.question.contentService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class contentController {

    @Autowired
    contentService contentService;

    @GetMapping("/question/getContent")
    public CommonResult<questionContentVO> getQuestionById(@RequestParam("id") int id) {
        return contentService.getQuestionById(id);
    }

    @GetMapping(value = "/question-getContentList")
    public CommonResult<List<questionContentVO>> getQuestionList() {
        return contentService.getQuestionList();
    }

    @GetMapping(value = "/question-getContentList1")
    public CommonResult<List<questionContentVO>> getQuestionList1() {
        return contentService.getQuestionList1();
    }

//    @GetMapping (value = "/question-getContentListPage")
//    public CommonResult<IPage<questionContentVO>> getQuestionListPage(@RequestParam(value = "limit", required = false) Integer limit,
//                                                                      @RequestParam(value = "currentPage", required = false) Integer currentPage,
//                                                                      @RequestParam(value = "keyword", required = false)  String keyword){
//        return contentService.getQuestionListPage(limit,currentPage,keyword);
//    }

    @GetMapping(value = "/question-getContentListPage")
    public CommonResult<Page<questionContentVO>> getQuestionListPage1(@RequestParam(value = "limit", required = false) Integer limit,
                                                                      @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                      @RequestParam(value = "keyword", required = false) String keyword) {
        return contentService.getQuestionListPage1(limit, currentPage, keyword);
    }


    @RequestMapping("/question/addQuestionContent")
    public CommonResult<Void> insertQuestion(@RequestBody content content) {
        return contentService.addQuestion(content);
    }


    @RequestMapping("/question/delQuestionContent")
    public CommonResult<Void> removeQuestion(@RequestParam("id") int id) {
        return contentService.removeQuestion(id);
    }

    @RequestMapping("/question/updateQuestionContent")
    public CommonResult<Void> updateQuestion(@RequestBody content content) {
        return contentService.updateQuestion(content);
    }
}
