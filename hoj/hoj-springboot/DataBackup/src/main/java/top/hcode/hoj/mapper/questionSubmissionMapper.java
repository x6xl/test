package top.hcode.hoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import top.hcode.hoj.pojo.entity.question.submission;

@Mapper
@Repository
public interface questionSubmissionMapper extends BaseMapper<submission> {

    @Select("select * from question_submission s ,question_content c where s.question_id  = c.question_id and c.question_id = #{question_id} order by submit_time desc LIMIT 1;")
    submission getSubmissionByQid(@RequestParam("question_id") String questionId);
}
