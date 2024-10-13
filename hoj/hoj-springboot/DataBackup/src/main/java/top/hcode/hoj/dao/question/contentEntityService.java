package top.hcode.hoj.dao.question;

import com.baomidou.mybatisplus.extension.service.IService;
import top.hcode.hoj.pojo.dto.QuestionDTO;
import top.hcode.hoj.pojo.entity.question.content;
import top.hcode.hoj.pojo.vo.questionContentVO;

import java.util.List;

public interface contentEntityService extends IService<content> {
    questionContentVO getQuestionById(int id);

    questionContentVO getQuestionByQid(String questionId);

    List<questionContentVO> getQuestionList();

    boolean adminAddQuestion(QuestionDTO questionDTO);

    boolean adminUpdateQuestion(QuestionDTO questionDTO);
}
