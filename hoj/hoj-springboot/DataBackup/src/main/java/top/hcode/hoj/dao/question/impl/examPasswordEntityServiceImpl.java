package top.hcode.hoj.dao.question.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.hcode.hoj.dao.question.examPasswordEntityService;
import top.hcode.hoj.mapper.examPasswordMapper;
import top.hcode.hoj.pojo.entity.question.examPassword;

@Service
public class examPasswordEntityServiceImpl extends ServiceImpl<examPasswordMapper, examPassword> implements examPasswordEntityService {
}
