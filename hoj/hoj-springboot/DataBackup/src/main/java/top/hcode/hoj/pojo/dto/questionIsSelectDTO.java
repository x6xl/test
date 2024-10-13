package top.hcode.hoj.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class questionIsSelectDTO {

    private String questionId;
    /**
     * 这个问题是否被选为了考试中的题目.默认是false，经过前端修改后可能会变成true，
     * 传回来用于创建问题
     */
    private Boolean checked;
}
