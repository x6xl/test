package top.hcode.hoj.pojo.entity.question;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("question_type")
public class type {

    @TableField("id")
    private int id;

    @TableField("question_type")
    private int  question_type;//题目类型，1为单选题、2为多选题、3为判断题、4为填空题、5为简答题、6为代码编程题
}
