package top.hcode.hoj.dao.question.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.hcode.hoj.dao.question.optionsEntityService;
import top.hcode.hoj.mapper.questionOptionsMapper;
import top.hcode.hoj.pojo.entity.question.options;

import java.util.List;

@Service
public class optionsEntityServiceImpl extends ServiceImpl<questionOptionsMapper, options> implements optionsEntityService {

    @Autowired
    questionOptionsMapper questionOptionsMapper;
    @Override
    public List<options> getOptionsByQid(String questionId) {
        return questionOptionsMapper.getOptionsByQid(questionId);
    }
}
