package top.hcode.hoj.pojo.entity.question;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@TableName("question_feedback")
public class feedback {
   @TableField("id")
    private int id;

   @TableField("question_id")
    private String questionId;

    @TableField("user_id")
    private String userId;

    @TableField("question_type")
    private int questionType;//题目类型，1为单选题、2为多选题、3为判断题、4为填空题、5为简答题、6为代码编程题

    @TableField("judge_result")
    private String judgeResult;

    @TableField("question_score")
    private int questionScore;

    @TableField("judge_time")
    private LocalDateTime judgeTime;

    @TableField("right_answer")
    private String rightAnswer;

    @TableField("user_answer")
    private String userAnswer;


 public feedback() {
  this.judgeTime = LocalDateTime.now();
 }
}
