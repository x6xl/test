package top.hcode.hoj.service.question;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.RequestParam;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.dto.examFinishDTO;
import top.hcode.hoj.pojo.dto.examEmailDTO;
import top.hcode.hoj.pojo.entity.question.exam;
import top.hcode.hoj.pojo.entity.question.examRecord;
import top.hcode.hoj.pojo.vo.examListVO;
import top.hcode.hoj.pojo.vo.examRecordDetailVO;
import top.hcode.hoj.pojo.vo.examRecordVO;
import top.hcode.hoj.pojo.vo.examVO;

import java.util.HashMap;
import java.util.List;

public interface examService {
    CommonResult<examVO> getExam(String examId,String password);

    CommonResult<List<examRecordVO>> getExamRecordList();

    CommonResult<examRecord> judgeExam(String examId, examFinishDTO examFinishDTO);

    CommonResult<examRecordDetailVO> getExamRecordDetail(String examId);

    CommonResult<List<examListVO>> examList();

    CommonResult<Void> ExamSentPassword(examEmailDTO examEmailDTO);

    CommonResult<List<HashMap<String, exam>>> getExamNotice();

    CommonResult<List<String>> examGetProblemId(String examId);
}
