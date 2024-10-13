package top.hcode.hoj.controller.file;


import com.baomidou.mybatisplus.extension.api.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.service.file.examFileService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/api/admin/question")
public class ExamFileController {
    @Resource
    examFileService examFileService;

    @RequestMapping("/get-examRecord-excel")
//    @RequiresAuthentication
//    @RequiresRoles("root")
    public void getExamRecordExcel(@RequestParam("exam_id") String examId, HttpServletResponse response) throws IOException {
        examFileService.getExamRecordExcel(examId, response);
    }

}
