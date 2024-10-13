package top.hcode.hoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import top.hcode.hoj.pojo.entity.question.options;

import java.util.List;

@Mapper
@Repository
public interface questionOptionsMapper extends BaseMapper<options> {

//    @Select("select o.id,option_content,o.question_id, o.author, o.author_id from question_options o ,question_content c where o.question_id = c.question_id and c.question_id = #{question_id};")
//    @Select("select o.id,option_content,o.question_id, o.author, o.author_id from question_options o ,question_content c where o.question_id = #{question_id};")
    @Select("select * from question_options where question_id = #{question_id};")
    List<options> getOptionsByQid(@Param("question_id") String question_id);


}
