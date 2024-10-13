package top.hcode.hoj.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import top.hcode.hoj.pojo.entity.question.exam;
import top.hcode.hoj.pojo.entity.question.options;

import java.util.List;

@ApiModel(value = "选择题VO", description = "")
@Data
public class examSelectQuestionVO {

    @ApiModelProperty(value = "题目id")
    private String questionId;

    @ApiModelProperty(value = "题目内容")
    private List<questionContentVO> questionContentVOs;

    @ApiModelProperty(value = "选项内容")
    private List<options> options;
}
