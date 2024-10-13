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
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("exam_image_shot")
@Document(indexName = "examimageshot",shards = 1,replicas = 0)
public class examImageShot implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @org.springframework.data.annotation.Id
    private String Id;

    @TableField("exam_id")
    @Field(type = FieldType.Keyword)
    private String examId;

    @TableField("exam_joiner_id")
    @Field(type = FieldType.Keyword)
    private String examJoinerId;

    @TableField("name")
    @Field(type = FieldType.Keyword)
    private String name;

    @TableField("folder_path")
    @Field(type = FieldType.Keyword)
    private String folderPath;

    @TableField("file_path")
    @Field(type = FieldType.Keyword)
    private String filePath;

    @TableField("shot_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Field(type = FieldType.Date)
    private LocalDateTime shotTime;

}
