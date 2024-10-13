package top.hcode.hoj.dao.question;

import com.baomidou.mybatisplus.extension.service.IService;
import top.hcode.hoj.pojo.entity.question.feedback;
import top.hcode.hoj.pojo.entity.question.submission;

public interface feedbackEntityService extends IService<feedback> {

     feedback getFeedbackByQid(String questionId);
}
