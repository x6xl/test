package top.hcode.hoj.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import top.hcode.hoj.pojo.entity.question.exam;

import java.util.List;

@ApiModel(value = "管理员卷子VO", description = "")
@Data
public class adminExamVO {
    @ApiModelProperty(value = "题目内容")
    private List<examQuestionVO> QuestionVOs;

    @ApiModelProperty(value = "卷子内容")
    private exam exam;

    @ApiModelProperty(value = "考试密码")
    private String password;

    @ApiModelProperty(value = "代码题id")
    private List<String> problemId;
}
