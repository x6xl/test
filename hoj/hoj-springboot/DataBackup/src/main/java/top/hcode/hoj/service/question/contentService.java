package top.hcode.hoj.service.question;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.data.domain.Page;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.entity.question.content;
import top.hcode.hoj.pojo.vo.questionContentVO;

import java.util.List;

public interface contentService {
    CommonResult<questionContentVO> getQuestionById(int id);

    CommonResult<Void> addQuestion(content content);

    CommonResult<Void> removeQuestion(int id);

    CommonResult<Void> updateQuestion(content content);


    CommonResult<List<questionContentVO>> getQuestionList();

    CommonResult<IPage<questionContentVO>> getQuestionListPage(Integer limit,Integer currentPage,String keyword);

    CommonResult<List<questionContentVO>> getQuestionList1();

    CommonResult<Page<questionContentVO>> getQuestionListPage1(Integer limit, Integer currentPage, String keyword);
}
