package top.hcode.hoj.dao.question.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.hcode.hoj.dao.question.examRecordEntityService;
import top.hcode.hoj.mapper.examRecordMapper;
import top.hcode.hoj.pojo.entity.question.examRecord;

@Service
public class examRecordEntityServiceImpl extends ServiceImpl<examRecordMapper, examRecord> implements examRecordEntityService {


}
