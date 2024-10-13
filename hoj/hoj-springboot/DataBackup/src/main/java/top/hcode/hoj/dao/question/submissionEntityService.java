package top.hcode.hoj.dao.question;

import com.baomidou.mybatisplus.extension.service.IService;
import top.hcode.hoj.pojo.entity.question.submission;

public interface submissionEntityService extends IService<submission> {
    submission getSubmissionByQid(String questionId);
}
