package top.hcode.hoj.service.question.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusForbiddenException;
import top.hcode.hoj.common.exception.StatusNotFoundException;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.common.result.ResultStatus;
import top.hcode.hoj.manager.question.optionsManager;
import top.hcode.hoj.pojo.entity.question.options;
import top.hcode.hoj.service.question.optionsService;

import java.util.List;

@Service
public class optionsServiceImpl implements optionsService {
    @Autowired
    optionsManager optionsManager;
    @Override
    public CommonResult<List<options>> getOptionsByQid(String questionId) {
        try{
            return CommonResult.successResponse(optionsManager.getOptionsByQid(questionId));
        }catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }

    }

    @Override
    public CommonResult<Void> addOptions(options options) {
        try{
            optionsManager.addOptions(options);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }
    }

    @Override
    public CommonResult<Void> removeOptionsById(int id) {
        try{
            optionsManager.removeOptionsById(id);
            return CommonResult.successResponse();
        }catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }

    }

    @Override
    public CommonResult<Void> updateOptionsById(options options) {
        try{
            optionsManager.updateOptionsById(options);
            return CommonResult.successResponse();
        }catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }

    }
}
