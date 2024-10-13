package top.hcode.hoj.repository;


import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.hcode.hoj.pojo.entity.question.examImageShot;

public interface examImageShotRepository extends ElasticsearchRepository<examImageShot,String> {

}
