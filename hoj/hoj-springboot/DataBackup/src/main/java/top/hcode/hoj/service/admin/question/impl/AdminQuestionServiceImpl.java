package top.hcode.hoj.service.admin.question.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusForbiddenException;
import top.hcode.hoj.common.exception.StatusNotFoundException;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.common.result.ResultStatus;
import top.hcode.hoj.manager.admin.question.AdminQuestionManager;
import top.hcode.hoj.pojo.dto.ExamAddProblemDTO;
import top.hcode.hoj.pojo.dto.QuestionDTO;
import top.hcode.hoj.pojo.dto.examCreateDTO;
import top.hcode.hoj.pojo.dto.questionIsSelectDTO;
import top.hcode.hoj.pojo.entity.question.content;
import top.hcode.hoj.pojo.entity.question.exam;
import top.hcode.hoj.pojo.entity.question.examRecord;
import top.hcode.hoj.pojo.entity.question.options;
import top.hcode.hoj.pojo.vo.adminExamVO;
import top.hcode.hoj.pojo.vo.examVO;
import top.hcode.hoj.service.admin.question.AdminQuestionService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public class AdminQuestionServiceImpl implements AdminQuestionService {
    @Autowired
    AdminQuestionManager adminQuestionManager;

    @Override
    public CommonResult<List<content>> getQuestionList() {
        return CommonResult.successResponse(adminQuestionManager.getQuestionList());
    }

    @Override
    public CommonResult<IPage<content>> getQuestionListPage(Integer limit, Integer currentPage, String keyword, int questionType) {
        IPage<content> questionList =  adminQuestionManager.getQuestionListPage(limit,currentPage,keyword,questionType);
        return CommonResult.successResponse(questionList);
    }

    @Override
    public CommonResult<content> getQuestionByQid(String questionId) {
        try {
            content content=adminQuestionManager.getQuestionByQid(questionId);
            return CommonResult.successResponse(content);
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> deleteQuestion(String questionId) {
        try {
            adminQuestionManager.deleteQuestion(questionId);
            return CommonResult.successResponse();
        }  catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> addQuestion(QuestionDTO questionDTO) {
        try {
            adminQuestionManager.addQuestion(questionDTO);
            return CommonResult.successResponse();
        }  catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> updateQuestion(QuestionDTO questionDTO) {
        try {
            adminQuestionManager.updateQuestion(questionDTO);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<List<options>> getOptionsByQid(String questionId) {
            return CommonResult.successResponse(adminQuestionManager.getOptionsByQid(questionId));
    }

    @Override
    public CommonResult<List<content>> getQuestionListFind(String keyword) {
        List<content> questionList =  adminQuestionManager.getQuestionListFind(keyword);
        return CommonResult.successResponse(questionList);
    }

    @Override
    public CommonResult<exam> createExam(examCreateDTO examCreateDTO) {
        try{adminQuestionManager.createExam(examCreateDTO);
        return CommonResult.successResponse();
        }
        catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<exam> updateExam(examCreateDTO examCreateDTO) {
        try{
        adminQuestionManager.updateExam(examCreateDTO);
        return CommonResult.successResponse();
        }catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
        catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> deleteExam(String examId) {
        try{
            adminQuestionManager.deleteExam(examId);
            return CommonResult.successResponse();
        }catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
        catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<List<exam>> examList() {
        return CommonResult.successResponse(adminQuestionManager.examList());
    }

    @Override
    public CommonResult<adminExamVO> getExam(String examId) {
        try{
            return CommonResult.successResponse(adminQuestionManager.getExam(examId));
        }catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }
    }

    @Override
    public CommonResult<Void> deleteOption(int id) {
            adminQuestionManager.deleteOption(id);
            return CommonResult.successResponse();
    }


    @RabbitListener(queuesToDeclare = @Queue("simple"))
    public String simpleSend(String message) {
        System.out.println(message);
        return null;
    }

    @Override
    public CommonResult<Void> addQuestions(List<QuestionDTO> questionDTOs) {
        try {
            adminQuestionManager.addQuestions(questionDTOs);
            return CommonResult.successResponse();
        }  catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> deleteQuestions(List<questionIsSelectDTO> questionIsSelectDTOs) {
        try {
            adminQuestionManager.deleteQuestions(questionIsSelectDTOs);
            return CommonResult.successResponse();
        }  catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<List<examRecord>> examRecordList(String examId, int sort,String keyword) {
        return CommonResult.successResponse(adminQuestionManager.examRecordList(examId,sort,keyword));
    }

    @Override
    public CommonResult<Void> addFillQuestion(content content) {
        try {
            adminQuestionManager.addFillQuestion(content);
            return CommonResult.successResponse();
        }  catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> updateFillQuestion(content content) {
        try {
            adminQuestionManager.updateFillQuestion(content);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<Void> deleteFillQuestion(String questionId) {
        try {
            adminQuestionManager.deleteFillQuestion(questionId);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> examAddProblem(ExamAddProblemDTO examAddProblemDTO) {
        try {
            adminQuestionManager.examAddProblem(examAddProblemDTO);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<List<String>> examGetProblemId(String examId) {
        return CommonResult.successResponse(adminQuestionManager.examGetProblemId(examId));
    }

    @Override
    public CommonResult<List<content>> getQuestionList1() {
        return CommonResult.successResponse(adminQuestionManager.getQuestionList1());
    }

    @Override
    public CommonResult<Page<content>> getQuestionListPage1(Integer limit, Integer currentPage, String keyword, int questionType) {
        Page<content> questionList =  adminQuestionManager.getQuestionListPage1(limit,currentPage,keyword,questionType);
        return CommonResult.successResponse(questionList);
    }

}
