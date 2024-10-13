package top.hcode.hoj.pojo.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.checkerframework.checker.formatter.qual.Format;

import java.time.LocalDateTime;

@ApiModel(value = "题目列表查询对象QuestionVO", description = "")
@Data
public class questionContentVO {
    @ApiModelProperty(value = "id")
    private int id;

    @ApiModelProperty(value = "题目id")
    private String questionId;

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty(value = "内容")
    private String questionContent;

    @ApiModelProperty(value = "作者id")
    private String authorId;

    @ApiModelProperty(value = "分数")
    private int questionScore;

    @ApiModelProperty(value = "题目类型")
    private int questionType;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
