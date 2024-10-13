package top.hcode.hoj.pojo.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)

@HeadRowHeight(30)  //表头行高
@ContentRowHeight(15)  //内容行高
@ColumnWidth(18)  //列宽
@ContentFontStyle(fontHeightInPoints = (short) 12) //字体大小
public class ExamRecordExcelVO {

    @ApiModelProperty(value = "考试Id")
    @ExcelProperty("考试Id")
    private String examId;

    @ApiModelProperty(value = "考生id")
    @ExcelProperty("考生id")
    private String examJoinerId;

    @ApiModelProperty(value = "考试得分")
    @ExcelProperty("考试得分")
    private Integer examJoinScore;

    @ApiModelProperty(value = "参加考试时间")
    @ExcelProperty("参加考试时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date examJoinDate;


    @ApiModelProperty(value = "考试时长")
    @ExcelProperty("考试时长")
    private Integer examTimeCost;


    @ApiModelProperty(value = "考生答案")
    @ExcelProperty("考生答案")
    private String answerOptionIds;
}
