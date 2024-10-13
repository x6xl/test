package top.hcode.hoj.dao.question.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.hcode.hoj.dao.question.feedbackEntityService;
import top.hcode.hoj.mapper.questionFeedbackMapper;
import top.hcode.hoj.pojo.entity.question.feedback;
import top.hcode.hoj.pojo.entity.question.submission;


@Service
public class feedbackEntityServiceImpl extends ServiceImpl<questionFeedbackMapper, feedback> implements feedbackEntityService {
    @Autowired
    questionFeedbackMapper questionFeedbackMapper;

    @Override
    public feedback getFeedbackByQid(String questionId) {
        return questionFeedbackMapper.getFeedbackByQid(questionId);
    }
}
