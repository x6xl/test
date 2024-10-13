package top.hcode.hoj.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import top.hcode.hoj.pojo.entity.question.content;
import top.hcode.hoj.pojo.entity.question.options;

import java.util.List;

@Data
@Accessors(chain = true)
public class QuestionDTO {

    private content content;

    private List<options> options;
}
