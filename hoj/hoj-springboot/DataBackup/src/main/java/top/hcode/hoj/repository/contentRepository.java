package top.hcode.hoj.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.hcode.hoj.pojo.entity.question.content;

public interface contentRepository extends ElasticsearchRepository<content,Integer> {
    void deleteByQuestionId(String questionId);
}
