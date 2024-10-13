package top.hcode.hoj.pojo.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class questionOptionFileDTO implements Serializable {
    @ExcelProperty(value = "questionId")
    private String questionId;
    @ExcelProperty(value = "optionContent")
    private String optionContent;
    @ExcelProperty(value = "author")
    private String author;
    @ExcelProperty(value = "authorId")
    private String authorId;
}