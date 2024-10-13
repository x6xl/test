package top.hcode.hoj.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import top.hcode.hoj.pojo.entity.question.exam;

@ApiModel(value = "考试列表VO", description = "")
@Data
public class examListVO {
    @ApiModelProperty(value = "卷子内容")
    private exam exam;

    Boolean isJoined;
}
