package top.hcode.hoj.service.file.impl;

import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.hcode.hoj.common.exception.StatusSystemErrorException;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.manager.file.examFileManager;
import top.hcode.hoj.service.file.examFileService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class examFileServiceImpl implements examFileService {
    @Resource
    examFileManager examFileManager;


    @Override
    public void getExamRecordExcel(String examId, HttpServletResponse response) throws IOException {
        examFileManager.getExamRecordExcel(examId,response);
    }

}
