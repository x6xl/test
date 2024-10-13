package top.hcode.hoj.pojo.entity.question;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;


@Data
@TableName("question_submission")
public class submission {

    @TableField("id")
    private int id;

    @TableField("question_id")
    private String questionId;

    @TableField("user_id")
    private String userId;

    @TableField("question_type")
    private int questionType;//题目类型，1为单选题、2为多选题、3为判断题、4为填空题、5为简答题、6为代码编程题

    @TableField("submit_time")
    private LocalDateTime submitTime;

    @TableField("submit_result")
    private String submitResult;


    public submission() {
        this.submitTime = LocalDateTime.now();
    }
}
