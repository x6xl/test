package top.hcode.hoj.pojo.dto;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class examCreateDTO {

    private String examId;

    private String examName;

    private String examDescription;

    private List<questionIsSelectDTO> questions;

    /**
     * 考试时长，单位分钟
     */
    private Integer examTimeLimit;

    /**
     * 单选题的分数
     */
    private Integer examScoreRadio;

    private Integer examScoreCode;

    private Integer examScoreFill;
    /**
     * 多选题的分数
     */
    private Integer examScoreCheck;

    /**
     * 判断题每题的分数
     */
    private Integer examScoreJudge;
    @JsonFormat(timezone = "GMT+8")
    private LocalDateTime examStartDate;
    @JsonFormat( timezone = "GMT+8")
    private LocalDateTime examEndDate;

    private String password;
}
