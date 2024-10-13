package top.hcode.hoj.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.dao.question.examPasswordEntityService;
import top.hcode.hoj.pojo.dto.examEmailDTO;
import top.hcode.hoj.pojo.entity.question.examPassword;
import top.hcode.hoj.validator.CommonValidator;

import javax.annotation.Resource;
import java.io.IOException;

@Component
@RabbitListener(queues = "examPasswordEmail")
public class examEmailUtils {
    @Resource
    JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String username;

    @Resource
    examPasswordEntityService examPasswordEntityService;

    @Resource
    RedisUtils redisUtils;

    @RabbitHandler
    public void ExamSentPassword(examEmailDTO examEmailDTO) throws StatusFailException {
        String email = examEmailDTO.getEmail();
        String examId = examEmailDTO.getExamId();

            String recordKey = "examPassword:" + examId;
            String password = (String) redisUtils.get(recordKey);
            if (password == null) {
                QueryWrapper<examPassword> examPasswordQueryWrapper = new QueryWrapper<>();
                examPasswordQueryWrapper.eq("exam_id", examId);
                examPassword examPassword = examPasswordEntityService.getOne(examPasswordQueryWrapper);
                if (examPassword != null) {
                    password = examPassword.getPassword();
                    redisUtils.set(recordKey, password);
                } else {
                    throw new StatusFailException("未找到考试密码记录");
                }
            }

            sendPasswordEmail(email,password);
    }


    @Async
    public void sendPasswordEmail(String email, String password) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
                        mailMessage.setFrom(username);
            mailMessage.setTo(email);
            mailMessage.setSubject("考试密码");
            mailMessage.setText("您的本次考试密码是：" + password);
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            e.printStackTrace(); // 记录日志
        }
    }


}
