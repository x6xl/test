package top.hcode.hoj.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ExamAddProblemDTO {

    private String examId;

    private List<String> problemId;
}
