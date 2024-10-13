package top.hcode.hoj.service.file;

import org.springframework.web.multipart.MultipartFile;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusForbiddenException;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.entity.question.content;

import java.io.IOException;

public interface questionsFileService {
    CommonResult<Void> importQuestion(MultipartFile file) throws IOException, StatusForbiddenException, StatusFailException;
}
