package top.hcode.hoj.dao.question.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.hcode.hoj.dao.question.submissionEntityService;
import top.hcode.hoj.mapper.questionSubmissionMapper;
import top.hcode.hoj.pojo.entity.question.submission;

@Service
public class submissionEntityServiceImpl extends ServiceImpl<questionSubmissionMapper, submission> implements submissionEntityService {
    @Autowired
    questionSubmissionMapper questionSubmissionMapper;

    @Override
    public submission getSubmissionByQid(String questionId) {
        return questionSubmissionMapper.getSubmissionByQid(questionId);
    }
}
