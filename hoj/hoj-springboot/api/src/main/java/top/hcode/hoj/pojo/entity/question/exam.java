package top.hcode.hoj.pojo.entity.question;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("exam")

public class exam {
    @TableId
    private String examId;

    @TableField("exam_name")
    private String examName;

    @TableField("exam_description")
    private String examDescription;

    @TableField("problem_id")
    private String problemId;

    @TableField("question_id")
    private String questionId;

    @TableField("exam_score")
    private Integer examScore;

    @TableField("exam_score_radio")
    private Integer examScoreRadio;

    @TableField("exam_score_check")
    private Integer examScoreCheck;

    @TableField("exam_score_fill")
    private Integer examScoreFill;

    @TableField("exam_score_code")
    private Integer examScoreCode;

    @TableField("exam_score_judge")
    private Integer examScoreJudge;

    @TableField("exam_creator_id")
    private String examCreatorId;

    @TableField("exam_time_limit")
    private Integer examTimeLimit;

    @TableField("exam_start_date")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime examStartDate;

    @TableField("exam_end_date")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime examEndDate;

    @TableField("create_time")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @TableField("update_time")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;


    public exam() {
        this.createTime = LocalDateTime.now();
        this.updateTime=LocalDateTime.now();
    }
}
