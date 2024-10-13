package top.hcode.hoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.w3c.dom.ls.LSOutput;
import top.hcode.hoj.pojo.entity.question.content;
import top.hcode.hoj.pojo.vo.questionContentVO;

import java.util.List;


@Mapper
@Repository
public interface questionContentMapper extends BaseMapper<content> {
    @Select("select id,question_id,author,author_id,question_content,question_type,create_time from question_content where id =#{id};")
    questionContentVO getQuestion_contentById(@Param("id") int id);


    @Select("select * from question_content where question_id =#{question_id};")
    content getQuestion_contentByQid(@Param("question_id") String question_id);


    @Select("select id,question_id,author,author_id,question_content,question_score,question_type,create_time from question_content")
    List<questionContentVO> getQuestionList();

    @Select("select id,question_id,author,author_id,question_content,question_score,question_type,create_time from question_content where question_id =#{question_id};")
    questionContentVO getQuestionByQid(@Param("question_id") String question_id);

}
