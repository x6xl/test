package top.hcode.hoj.pojo.entity.question;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("question_options")
public class options {

    @TableField("id")
    private int id;

    @TableField("question_id")
    private String questionId;

    @TableField("option_content")
    private String optionContent;

    @TableField("author")
    private String author;

    @TableField("author_id")
    private String authorId;
}
