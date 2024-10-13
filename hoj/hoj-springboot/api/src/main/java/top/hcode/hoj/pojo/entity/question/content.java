package top.hcode.hoj.pojo.entity.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;


@Data

@TableName("question_content")
@AllArgsConstructor
@Document(indexName = "content",shards = 1,replicas = 0)
public class content {

    @TableId(type = IdType.AUTO)
    @Id
    private Integer id;

    @TableField("question_id")
    @Field(type = FieldType.Keyword)
    private String questionId;

    @TableField("author")
    @Field(type = FieldType.Keyword)
    private String author;

    @TableField("author_id")
    @Field(type = FieldType.Keyword)
    private String authorId;

    @TableField("question_type")
    @Field(type = FieldType.Integer)
    private Integer questionType;//题目类型，1为单选题、2为多选题、3为判断题、4为填空题、5为简答题、6为代码编程题

    @Field(type = FieldType.Integer)
    @TableField("question_score")
    private Integer questionScore;

    @TableField("question_content")
    @Field(type = FieldType.Keyword)
    private String questionContent;

    @TableField("right_answer")
    @Field(type = FieldType.Keyword)
    private String rightAnswer;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Field(type = FieldType.Date)
    private LocalDateTime createTime;


    public content() {
        this.createTime = LocalDateTime.now();
    }

}
