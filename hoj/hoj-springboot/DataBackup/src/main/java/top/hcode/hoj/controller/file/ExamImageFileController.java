package top.hcode.hoj.controller.file;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.hcode.hoj.common.exception.StatusSystemErrorException;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.dao.question.examImageShotEntityService;
import top.hcode.hoj.pojo.entity.question.examImageShot;
import top.hcode.hoj.repository.examImageShotRepository;
import top.hcode.hoj.service.file.examImageFileService;
import top.hcode.hoj.service.file.impl.examImageFileSeiviceImpl;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/examImage")
public class ExamImageFileController {
    @Resource
    examImageFileService examImageFileService;
    @Resource
    examImageShotEntityService examImageShotEntityService;
    @Resource
    examImageShotRepository examImageShotRepository;


    @RequestMapping("/add-examImageShot")
    public CommonResult<Map<Object, Object>> addExamImageShot(@RequestPart("file") MultipartFile image, @RequestParam("exam_id")String examId) throws StatusSystemErrorException, IOException {
        return examImageFileService.addExamImageShot(image,examId);
    }


    @GetMapping("/get-examAllImageShot")
    public CommonResult<Map<String, List<HashMap<String, Object>>>> getAllExamImageShot(@RequestParam("exam_id")String examId){
        return examImageFileService.getAllExamImageShot(examId);
    }


    @RequestMapping("/saveToes")
    public String saveToEs(){
        List<examImageShot> list = examImageShotEntityService.list();
        examImageShotRepository.saveAll(list);
        return "ok";
    }

}
