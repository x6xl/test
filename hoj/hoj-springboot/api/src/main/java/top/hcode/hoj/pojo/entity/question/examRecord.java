package top.hcode.hoj.pojo.entity.question;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

@Data
@TableName("exam_record")
public class examRecord {
    @TableId
    private String examRecordId;
    /**
     * 参与的考试的id
     */
    @TableField("exam_id")
    private String examId;

    /**
     * 参与者，即user的id
     */
    @TableField("exam_joiner_id")
    private String examJoinerId;
    /**
     * 参加考试的日期
     */
    @TableField("exam_join_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date examJoinDate;
    /**
     * 考试耗时(秒)
     */
    @TableField("exam_time_cost")
    private Integer examTimeCost;
    /**
     * 考试得分
     */
    @TableField("exam_join_score")
    private Integer examJoinScore;

    /**
     * 考生作答地每个题目的选项(题目和题目之间用_分隔，题目有多个选项地话用-分隔),用于查看考试详情
     */
    @TableField("answer_option_ids")
    private String answerOptionIds;
}
