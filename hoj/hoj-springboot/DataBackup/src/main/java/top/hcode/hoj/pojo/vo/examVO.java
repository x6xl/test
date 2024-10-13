package top.hcode.hoj.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import top.hcode.hoj.pojo.entity.question.exam;

import java.util.List;

@ApiModel(value = "卷子VO", description = "")
@Data
public class examVO {
    @ApiModelProperty(value = "题目内容")
    private List<examSelectQuestionVO> selectQuestionVOs;

    @ApiModelProperty(value = "卷子内容")
    private exam exam;

    @ApiModelProperty(value = "代码题id")
    private List<String> problemId;
}