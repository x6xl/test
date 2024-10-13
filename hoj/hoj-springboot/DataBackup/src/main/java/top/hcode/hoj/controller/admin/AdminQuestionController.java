package top.hcode.hoj.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.dto.ExamAddProblemDTO;
import top.hcode.hoj.pojo.dto.QuestionDTO;
import top.hcode.hoj.pojo.dto.examCreateDTO;
import top.hcode.hoj.pojo.dto.questionIsSelectDTO;
import top.hcode.hoj.pojo.entity.question.content;
import top.hcode.hoj.pojo.entity.question.exam;
import top.hcode.hoj.pojo.entity.question.examRecord;
import top.hcode.hoj.pojo.entity.question.options;
import top.hcode.hoj.pojo.vo.adminExamVO;
import top.hcode.hoj.pojo.vo.examRecordVO;
import top.hcode.hoj.pojo.vo.examVO;
import top.hcode.hoj.repository.contentRepository;
import top.hcode.hoj.service.admin.question.AdminQuestionService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/admin/question")
public class AdminQuestionController {
    @Autowired
    AdminQuestionService adminQuestionService;

    @Autowired
    RabbitTemplate rabbitTemplate;


//    //直接获取全表
//    @GetMapping("/get-question-list")
////    @RequiresAuthentication
////    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
//    public CommonResult<List<content>> getQuestionList(){
//        return adminQuestionService.getQuestionList();
//    }

    //直接获取全表
    @GetMapping("/get-question-list")
//    @RequiresAuthentication
//    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<List<content>> getQuestionList1(){
        return adminQuestionService.getQuestionList1();
    }

//    //分页、搜索条件的获取表
//    @GetMapping("/get-question-list-page")
////    @RequiresAuthentication
////    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
//    public CommonResult<IPage<content>> getQuestionListPage(
//            @RequestParam(value = "limit", required = false) Integer limit,
//            @RequestParam(value = "currentPage", required = false) Integer currentPage,
//            @RequestParam(value = "keyword", required = false)  String keyword,
//            @RequestParam(value = "questionType", required = false)  int questionType){
//        return adminQuestionService.getQuestionListPage(limit,currentPage,keyword,questionType);
//    }

    @GetMapping("/get-question-list-page")
//    @RequiresAuthentication
//    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Page<content>> getQuestionListPage1(
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "currentPage", required = false) Integer currentPage,
            @RequestParam(value = "keyword", required = false)  String keyword,
            @RequestParam(value = "questionType", required = false)  int questionType){
        return adminQuestionService.getQuestionListPage1(limit,currentPage,keyword,questionType);
    }

    //搜索
    @GetMapping("/get-question-list-find")
//    @RequiresAuthentication
//    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<List<content>> getQuestionListFind(@RequestParam(value = "keyword", required = false)  String keyword){
        return adminQuestionService.getQuestionListFind(keyword);
    }

    @GetMapping("/get-questionByQid")
//    @RequiresAuthentication
//    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<content> getQuestionByQid(@RequestParam(value = "question_id", required = false)  String questionId){
        return adminQuestionService.getQuestionByQid(questionId);
    }
    //获取选项
    @GetMapping("/get-options")
//    @RequiresAuthentication
//    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<List<options>> getOptionsByQid(@RequestParam("question_id")String questionId){
        return adminQuestionService.getOptionsByQid(questionId);
    }
    @RequestMapping("/delete-questionByQid")
//    @RequiresAuthentication
//    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> deleteQuestion(@RequestParam(value = "question_id", required = false)  String questionId){
        return adminQuestionService.deleteQuestion(questionId);
    }

    @RequestMapping("/delete-questionByQids")
//    @RequiresAuthentication
//    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> deleteQuestions(@RequestBody List<questionIsSelectDTO> questionIsSelectDTOs){
        return adminQuestionService.deleteQuestions(questionIsSelectDTOs);
    }

    @RequestMapping("/delete-option")
//    @RequiresAuthentication
//    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> deleteOption(@RequestParam(value = "id", required = false) int id){
        return adminQuestionService.deleteOption(id);
    }


    @RequestMapping("/add-question")
//    @RequiresAuthentication
//    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> addQuestion(@RequestBody QuestionDTO questionDTO){
        return adminQuestionService.addQuestion(questionDTO);
    }

    @RequestMapping("/add-questions")
//    @RequiresAuthentication
//    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> addQuestions(@RequestBody List<QuestionDTO> questionDTOs){
        return adminQuestionService.addQuestions(questionDTOs);
    }

    @RequestMapping("/add-fillQuestion")
//    @RequiresAuthentication
//    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> addFillQuestion(@RequestBody content content){
        return adminQuestionService.addFillQuestion(content);
    }

    @RequestMapping("/update-fillQuestion")
//    @RequiresAuthentication
//    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> updateFillQuestion(@RequestBody content content){
        return adminQuestionService.updateFillQuestion(content);
    }

    @RequestMapping("/delete-fillQuestionByQid")
//    @RequiresAuthentication
//    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> deleteFillQuestion(@RequestParam(value = "question_id", required = false)  String questionId){
        return adminQuestionService.deleteFillQuestion(questionId);
    }

    @RequestMapping("/update-question")
//    @RequiresAuthentication
//    @RequiresRoles(value = {"root", "admin", "problem_admin"}, logical = Logical.OR)
    public CommonResult<Void> updateQuestion(@RequestBody QuestionDTO questionDTO){
        return adminQuestionService.updateQuestion(questionDTO);
    }

    @GetMapping("/exam-List")
    public CommonResult<List<exam>> examList(){
        return adminQuestionService.examList();
    }

    @GetMapping("/exam-record-List")
    public CommonResult<List<examRecord>> examRecordList(@RequestParam("exam_id")String examId,@RequestParam("sort")int sort,@RequestParam("keyword")String keyword){
        return adminQuestionService.examRecordList(examId,sort,keyword);
    }

    @RequestMapping("/exam-create")
    public CommonResult<exam> createExam(@RequestBody examCreateDTO examCreateDTO){
        return adminQuestionService.createExam(examCreateDTO);
    }

    @RequestMapping("/exam-update")
    public CommonResult<exam> updateExam(@RequestBody examCreateDTO examCreateDTO){
        return adminQuestionService.updateExam(examCreateDTO);
    }

    @GetMapping("/exam-getProblemId")
    public CommonResult<List<String>> examGetProblemId(@RequestParam("exam_id") String examId){
        return adminQuestionService.examGetProblemId(examId);
    }

    @RequestMapping("/exam-addProblem")
    public CommonResult<Void> examAddProblem(@RequestBody ExamAddProblemDTO examAddProblemDTO){
        return adminQuestionService.examAddProblem(examAddProblemDTO);
    }

    @RequestMapping("/exam-delete")
    public CommonResult<Void> deleteExam(@RequestParam("exam_id")String examId){
        return adminQuestionService.deleteExam(examId);
    }

    @GetMapping("/get-exam")
    public CommonResult<adminExamVO> getExam(@RequestParam("exam_id")String examId){
        return adminQuestionService.getExam(examId);
    }
}
