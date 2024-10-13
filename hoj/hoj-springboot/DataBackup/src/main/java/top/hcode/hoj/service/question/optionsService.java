package top.hcode.hoj.service.question;

import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.entity.question.options;

import java.util.List;

public interface optionsService {
    CommonResult<List<options>> getOptionsByQid(String questionId);

    CommonResult<Void> addOptions(options options);

    CommonResult<Void> removeOptionsById(int id);

    CommonResult<Void> updateOptionsById(options options);
}
