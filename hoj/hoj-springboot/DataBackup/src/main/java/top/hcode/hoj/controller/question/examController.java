package top.hcode.hoj.controller.question;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.checkerframework.checker.units.qual.C;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.dto.examFinishDTO;
import top.hcode.hoj.pojo.dto.examEmailDTO;
import top.hcode.hoj.pojo.entity.question.exam;
import top.hcode.hoj.pojo.entity.question.examRecord;
import top.hcode.hoj.pojo.vo.examListVO;
import top.hcode.hoj.pojo.vo.examRecordDetailVO;
import top.hcode.hoj.pojo.vo.examRecordVO;
import top.hcode.hoj.pojo.vo.examVO;
import top.hcode.hoj.service.question.examService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
public class examController {
    @Autowired
    examService examService;

    @GetMapping("/get-exam")
    public CommonResult<examVO> getExam(@RequestParam("exam_id")String examId,@RequestParam("password")String password){
        return examService.getExam(examId,password);
    }

    @GetMapping("/exam-getProblemId")
    public CommonResult<List<String>> examGetProblemId(@RequestParam("exam_id") String examId){
        return examService.examGetProblemId(examId);
    }

    @PostMapping("/finish-exam")
    public CommonResult<examRecord> finishExam(@RequestParam("exam_id") String examId, @RequestBody examFinishDTO examFinishDTO){
        return examService.judgeExam(examId,examFinishDTO);
    }

    @GetMapping("/exam/RecordList")
    public CommonResult<List<examRecordVO>> getExamRecordList(){
        return examService.getExamRecordList();
    }

    @GetMapping("/exam/RecordDetail")
    public CommonResult<examRecordDetailVO> getExamRecordDetail(@RequestParam("exam_id") String examId){
        return examService.getExamRecordDetail(examId);
    }

    @GetMapping("/exam-List")
    public CommonResult<List<examListVO>> examList(){
        return examService.examList();
    }

    @PostMapping("/exam-sentPassword")
    public CommonResult<Void> ExamSentPassword(@RequestBody examEmailDTO examEmailDTO){
        return examService.ExamSentPassword(examEmailDTO);
    }

    @GetMapping("/exam-notice")
    public CommonResult<List<HashMap<String, exam>>> getExamNotice(){
        return examService.getExamNotice();
    }

}
