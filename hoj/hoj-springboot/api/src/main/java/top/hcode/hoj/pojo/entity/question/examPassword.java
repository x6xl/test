package top.hcode.hoj.pojo.entity.question;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("exam_password")
public class examPassword {

    @TableId
    private String examId;

    @TableField("password")
    private String password;
}
