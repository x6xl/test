package top.hcode.hoj.controller.file;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusForbiddenException;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.service.file.questionsFileService;

import java.io.IOException;

@RestController
@RequestMapping("/api/file")
public class questionsFileController {
    @Autowired
    questionsFileService questionsFileService;

    @RequestMapping("/importQuestion")
    public CommonResult<Void> importQuestion(@RequestPart("file") MultipartFile file) throws StatusForbiddenException, StatusFailException, IOException {
        return questionsFileService.importQuestion(file);
    }
}
