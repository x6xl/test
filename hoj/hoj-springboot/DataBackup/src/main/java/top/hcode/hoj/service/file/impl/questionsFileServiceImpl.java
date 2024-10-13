package top.hcode.hoj.service.file.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusForbiddenException;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.manager.file.questionsFileManager;
import top.hcode.hoj.pojo.entity.question.content;
import top.hcode.hoj.service.file.questionsFileService;

import java.io.IOException;

@Service
public class questionsFileServiceImpl implements questionsFileService {
    @Autowired
    questionsFileManager questionsFileManager;

    @Override
    public CommonResult<Void> importQuestion(MultipartFile file) throws IOException, StatusForbiddenException, StatusFailException {
        questionsFileManager.importQuestion(file);
        return CommonResult.successResponse();
    }
}
