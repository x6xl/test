package top.hcode.hoj.dao.question.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.dao.question.examEntityService;
import top.hcode.hoj.mapper.examMapper;
import top.hcode.hoj.pojo.dto.questionIsSelectDTO;
import top.hcode.hoj.pojo.entity.question.exam;

@Service
public class examEntityServiceImpl extends ServiceImpl<examMapper, exam>implements examEntityService {
    @Autowired
    examMapper examMapper;

}
