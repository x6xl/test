package top.hcode.hoj.service.question.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusForbiddenException;
import top.hcode.hoj.common.exception.StatusNotFoundException;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.common.result.ResultStatus;
import top.hcode.hoj.manager.question.contentManager;
import top.hcode.hoj.pojo.entity.question.content;
import top.hcode.hoj.pojo.vo.questionContentVO;
import top.hcode.hoj.service.question.contentService;

import java.io.IOException;
import java.util.List;

@Service
public class contentServiceImpl implements contentService {
    @Autowired
    contentManager contentManager;
    @Override
    public CommonResult<questionContentVO> getQuestionById(int id) {
        try{
            return CommonResult.successResponse(contentManager.getQuestionContent(id));
        }catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }

    }

    @Override
    public CommonResult<Void> addQuestion(content content){
        try{
            contentManager.addQuestion(content);
            return CommonResult.successResponse();
        }
        catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FAIL);
        }
    }

    @Override
    public CommonResult<Void> removeQuestion(int id) {
        try{
            contentManager.removeQuestion(id);
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
    public CommonResult<Void> updateQuestion(content content) {
        try{
            contentManager.updateQuestion(content);
            return CommonResult.successResponse();
        }
        catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FAIL);
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }
    }

    @Override
    public CommonResult<List<questionContentVO>> getQuestionList() {
        try{
            return CommonResult.successResponse(contentManager.getQuestionList());
        }catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }
    }

    @Override
    public CommonResult<List<questionContentVO>> getQuestionList1() {
        try{
            return CommonResult.successResponse(contentManager.getQuestionList1());
        }catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CommonResult<Page<questionContentVO>> getQuestionListPage1(Integer limit, Integer currentPage, String keyword) {
        try{
            return CommonResult.successResponse(contentManager.getQuestionListPage1(limit,currentPage,keyword));
        }catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }
    }

    @Override
    public CommonResult<IPage<questionContentVO>> getQuestionListPage(Integer limit, Integer currentPage, String keyword) {
        try{
            return CommonResult.successResponse(contentManager.getQuestionListPage(limit,currentPage,keyword));
        }catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }
    }




}
