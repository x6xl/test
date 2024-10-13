package top.hcode.hoj.pojo.vo;


import io.swagger.annotations.ApiModel;
import lombok.Data;
import top.hcode.hoj.pojo.entity.question.examRecord;

import java.util.HashMap;
import java.util.List;


@ApiModel(value = "考试记录详情VO", description = "")
@Data
public class examRecordDetailVO {
    /**
     * 含有考试记录原始信息的对象
     */
    private examRecord examRecord;
    /**
     * 用户此次考试的作答信息, 键是题目的id，值是选项id的列表
     */
    private HashMap<String, List<String>> answersMap;

    /**
     * 用户每题作答结果的Map，键是问题的id，值是用户这题是否回答正确，True or False
     */
    private HashMap<String, String> resultsMap;

    /**
     * 正确答案，键是题目的id，值是正确答案的id组成的列表
     */
    private HashMap<String, List<String>> answersRightMap;

    private examVO examVO;
}
