package top.hcode.hoj.service.question.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusNotFoundException;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.common.result.ResultStatus;
import top.hcode.hoj.manager.question.examManager;
import top.hcode.hoj.pojo.dto.examFinishDTO;
import top.hcode.hoj.pojo.dto.examEmailDTO;
import top.hcode.hoj.pojo.entity.question.exam;
import top.hcode.hoj.pojo.entity.question.examRecord;
import top.hcode.hoj.pojo.vo.examListVO;
import top.hcode.hoj.pojo.vo.examRecordDetailVO;
import top.hcode.hoj.pojo.vo.examRecordVO;
import top.hcode.hoj.pojo.vo.examVO;
import top.hcode.hoj.service.question.examService;
import top.hcode.hoj.utils.examEmailUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Service
public class examServiceImpl implements examService {

    @Autowired
    examManager examManager;


    @Override
    public CommonResult<examVO> getExam(String examId,String password) {
        try{
            return CommonResult.successResponse(examManager.getExam(examId,password));
        }catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<List<examRecordVO>> getExamRecordList() {
        try{
            return CommonResult.successResponse(examManager.getExamRecordList());
        }catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }

    }

    @Override
    public CommonResult<examRecord> judgeExam(String examId, examFinishDTO examFinishDTO) {
        try{
            return CommonResult.successResponse(examManager.judgeExam(examId,examFinishDTO));
        }catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }
    }

    @Override
    public CommonResult<examRecordDetailVO> getExamRecordDetail(String examId) {
        try{
            return CommonResult.successResponse(examManager.getExamRecordDetail(examId));
        }catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }
    }

    @Override
    public CommonResult<List<examListVO>> examList() {
        return CommonResult.successResponse(examManager.examList());
    }

    @Override
    public CommonResult<Void> ExamSentPassword(examEmailDTO examEmailDTO) {
        try{
            return CommonResult.successResponse(examManager.ExamSentPassword(examEmailDTO));
        }catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<List<HashMap<String, exam>>> getExamNotice() {
        return  CommonResult.successResponse(examManager.getExamNotice());
    }

    @Override
    public CommonResult<List<String>> examGetProblemId(String examId) {
        return CommonResult.successResponse(examManager.examGetProblemId(examId));
    }

}
