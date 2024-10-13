package top.hcode.hoj.dao.question.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.hcode.hoj.dao.question.contentEntityService;
import top.hcode.hoj.exception.ProblemIDRepeatException;
import top.hcode.hoj.mapper.questionContentMapper;
import top.hcode.hoj.pojo.dto.QuestionDTO;
import top.hcode.hoj.pojo.entity.problem.Problem;
import top.hcode.hoj.pojo.entity.question.content;
import top.hcode.hoj.pojo.vo.questionContentVO;

import java.util.List;

@Service
public class contentEntityServiceImpl extends ServiceImpl<questionContentMapper, content> implements contentEntityService {

    @Autowired
    private questionContentMapper questionContentMapper;

    @Override
    public questionContentVO getQuestionById(int id) {
        return questionContentMapper.getQuestion_contentById(id);
    }

    @Override
    public questionContentVO getQuestionByQid(String questionId) {
        return questionContentMapper.getQuestionByQid(questionId);
    }

    @Override
    public List<questionContentVO> getQuestionList() {
        return questionContentMapper.getQuestionList();
    }

    @Override
    public boolean adminAddQuestion(QuestionDTO questionDTO) {
//        content content=questionDTO.getContent();
//
//        boolean isok=false;
//        if (content.getQuestionId()==null){//问题id为空，分配一个
//            content.setQuestionId(UUID.fastUUID().toString());
//            questionContentMapper.insert(content);
//            UpdateWrapper<content> contentUpdateWrapper = new UpdateWrapper<>();
//            contentUpdateWrapper.set("question_id", "Q" + content.getId());
//            contentUpdateWrapper.eq("id",content.getId());
//            questionContentMapper.update(null, contentUpdateWrapper);
//            content.setQuestionId("Q" + content.getId());
//            isok=true;
//        }else{//问题id不为空，检查问题id是否已存在
//            String questionId = content.getQuestionId().toUpperCase();
//            QueryWrapper<content> questionQueryWrapper = new QueryWrapper<>();
//            questionQueryWrapper.eq("question_id", questionId);
//            int existedProblem = questionContentMapper.selectCount(questionQueryWrapper);
//            if (existedProblem > 0) {
//                throw new ProblemIDRepeatException("The problem_id [" + questionId + "] already exists. Do not reuse it!");
//            }
//            content.setQuestionId(questionId);
//            questionContentMapper.insert(content);
//            isok=true;
//        }
//        if (isok){
//            return true;
//        }else {
//            return false;
//        }
        return false;
    }

    @Override
    public boolean adminUpdateQuestion(QuestionDTO questionDTO) {
        return false;
    }


}
