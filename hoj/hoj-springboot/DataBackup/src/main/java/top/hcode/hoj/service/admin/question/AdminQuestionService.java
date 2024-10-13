package top.hcode.hoj.service.admin.question;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.data.domain.Page;
import top.hcode.hoj.common.result.CommonResult;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface AdminQuestionService {
    CommonResult<List<content>> getQuestionList();

    CommonResult<IPage<content>> getQuestionListPage(Integer limit, Integer currentPage, String keyword,int questionType);

    CommonResult<content> getQuestionByQid(String questionId);

    CommonResult<Void> deleteQuestion(String questionId);

    CommonResult<Void> addQuestion(QuestionDTO questionDTO);

    CommonResult<Void> updateQuestion(QuestionDTO questionDTO);

    CommonResult<List<options>> getOptionsByQid(String questionId);

    CommonResult<List<content>> getQuestionListFind(String keyword);

    CommonResult<exam> createExam(examCreateDTO examCreateDTO);

    CommonResult<exam> updateExam(examCreateDTO examCreateDTO);

    CommonResult<Void> deleteExam(String examId);

    CommonResult<List<exam>> examList();

    CommonResult<adminExamVO> getExam(String examId);

    CommonResult<Void> deleteOption(int id);

    CommonResult<Void> addQuestions(List<QuestionDTO> questionDTOs);

    CommonResult<Void> deleteQuestions(List<questionIsSelectDTO> questionIsSelectDTOs);

    CommonResult<List<examRecord>> examRecordList(String examId, int sort,String keyword);

    CommonResult<Void> addFillQuestion(content content);

    CommonResult<Void> updateFillQuestion(content content);

    CommonResult<Void> deleteFillQuestion(String questionId);

    CommonResult<Void> examAddProblem(ExamAddProblemDTO examAddProblemDTO);

    CommonResult<List<String>> examGetProblemId(String examId);

    CommonResult<List<content>> getQuestionList1();

    CommonResult<Page<content>> getQuestionListPage1(Integer limit, Integer currentPage, String keyword, int questionType);

}
