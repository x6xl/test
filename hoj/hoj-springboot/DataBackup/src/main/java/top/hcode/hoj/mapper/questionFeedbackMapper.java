package top.hcode.hoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import top.hcode.hoj.pojo.entity.question.feedback;


@Mapper
@Repository
public interface questionFeedbackMapper extends BaseMapper<feedback> {

    @Select("select * from question_feedback f, question_content c where f.question_id  = c.question_id and f.question_id = #{question_id} order by judge_time desc LIMIT 1;")
    feedback getFeedbackByQid(@RequestParam("question_id") String questionId);
}
