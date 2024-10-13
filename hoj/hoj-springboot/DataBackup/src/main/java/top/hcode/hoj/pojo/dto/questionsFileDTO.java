package top.hcode.hoj.pojo.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class questionsFileDTO  implements Serializable{
    @ExcelProperty(value = "questionId")
    private String questionId;
    @ExcelProperty(value = "author")
    private String author;
    @ExcelProperty(value = "authorId")
    private String authorId;
    @ExcelProperty(value = "questionType")
    private int questionType;
    @ExcelProperty(value = "questionScore")
    private int questionScore;
    @ExcelProperty(value = "questionContent")
    private String questionContent;
    @ExcelProperty(value = "rightAnswer")
    private String rightAnswer;
//    @ExcelProperty(value = "createTime")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime createTime;
    @ExcelIgnore
    private List<questionOptionFileDTO> options;
}
