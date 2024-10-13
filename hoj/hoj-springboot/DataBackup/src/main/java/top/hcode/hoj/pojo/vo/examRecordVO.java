package top.hcode.hoj.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import top.hcode.hoj.pojo.entity.question.exam;
import top.hcode.hoj.pojo.entity.question.examRecord;

@ApiModel(value = "考试记录VO", description = "")
@Data
public class examRecordVO {
    @ApiModelProperty(value = "考试信息")
    private exam exam;

    @ApiModelProperty(value = "考试记录")
    private examRecord examRecord;
}
