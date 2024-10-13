package top.hcode.hoj.manager.question;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusForbiddenException;
import top.hcode.hoj.common.exception.StatusNotFoundException;
import top.hcode.hoj.dao.question.contentEntityService;
import top.hcode.hoj.mapper.questionContentMapper;
import top.hcode.hoj.pojo.entity.question.content;
import top.hcode.hoj.pojo.vo.questionContentVO;
import top.hcode.hoj.repository.contentRepository;
import top.hcode.hoj.shiro.AccountProfile;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class contentManager {

    @Autowired
    contentEntityService contentEntityService;
    @Autowired
    questionContentMapper questionContentMapper;

    @Resource
    contentRepository contentRepository;
    @Autowired
    private RestHighLevelClient client;

    public questionContentVO getQuestionContent(int id) throws StatusNotFoundException {

        QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper
                .select("id", "question_id")
                .eq("id", id);

        content content = contentEntityService.getOne(contentQueryWrapper);
        if (content == null) {
            throw new StatusNotFoundException("查询失败，该题已不存在！");
        }

        return contentEntityService.getQuestionById(id);
    }

    public content getQuestionContentByQid(String questionId) throws StatusNotFoundException {

        content content = questionContentMapper.getQuestion_contentByQid(questionId);
        if (content == null) {
            throw new StatusNotFoundException("查询失败，该题已不存在！");
        }
        return content;
    }

    public void addQuestion(content content) throws StatusForbiddenException, StatusFailException {
//
//        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
//
//        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
//        if (!isRoot
//                && !content.getAuthorId().equals(userRolesVo.getUid())) {
//            throw new StatusForbiddenException("对不起，您无权限操作！");
//        }

        boolean isOk = contentEntityService.saveOrUpdate(content);
        if (!isOk) {
            throw new StatusFailException("添加失败，请重新尝试！");
        }
    }


    public void removeQuestion(int id) throws StatusForbiddenException, StatusFailException, StatusNotFoundException {

        QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper
                .eq("id", id);

        content content = contentEntityService.getOne(contentQueryWrapper);
        if (content == null) {
            throw new StatusNotFoundException("删除失败，该题已不存在！");
        }

//        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
//
//        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
//        if (!isRoot
//                && !content.getAuthorId().equals(userRolesVo.getUid())) {
//            throw new StatusForbiddenException("对不起，您无权限操作！");
//        }

        boolean isOk = contentEntityService.remove(contentQueryWrapper);
        if (!isOk) {
            throw new StatusFailException("删除失败，请重新尝试！");
        }

    }


    public void updateQuestion(content content) throws StatusForbiddenException, StatusFailException, StatusNotFoundException {

        QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper
                .eq("id", content.getId());

        content content1 = contentEntityService.getOne(contentQueryWrapper);
        if (content1 == null) {
            throw new StatusNotFoundException("更新失败，该题已不存在！");
        }

//        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
//
//        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
//
//        if (!isRoot
//                && !content.getAuthorId().equals(userRolesVo.getUid())) {
//            throw new StatusForbiddenException("对不起，您无权限操作！");
//        }
        UpdateWrapper<content> contentUpdateWrapper = new UpdateWrapper<>();
        contentUpdateWrapper.set("question_id", content.getQuestionId())
                .set("author", content.getAuthor())
                .set("question_type", content.getQuestionType())
                .set("question_score", content.getQuestionScore())
                .set("question_content", content.getQuestionContent())
                .set("right_answer", content.getRightAnswer())
                .set("create_time", content.getCreateTime())
                .set("author_id", content.getAuthorId())
                .eq("id", content.getId());


        boolean isOk = contentEntityService.update(contentUpdateWrapper);
        if (!isOk) {
            throw new StatusFailException("更新失败，请重新尝试！");
        }


    }

    public List<questionContentVO> getQuestionList() throws StatusNotFoundException {
        List<questionContentVO> list = contentEntityService.getQuestionList();
        if (list == null) {
            throw new StatusNotFoundException("更新失败，请重新尝试！");
        }
        return list;
    }

    public IPage<questionContentVO> getQuestionListPage(Integer limit, Integer currentPage, String keyword) throws StatusNotFoundException {
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 2;

        IPage<content> iPage = new Page<>(currentPage, limit);
        IPage<content> questionList;

        QueryWrapper<content> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(keyword)) {
            final String key = keyword.trim();
            queryWrapper.and(wrapper -> wrapper.like("question_id", key).or()
                    .like("author", key).or()
                    .like("question_content", key));
            questionList = contentEntityService.page(iPage, queryWrapper);
        } else {
            questionList = contentEntityService.page(iPage, queryWrapper);
        }

        // 将 content 转换为 questionContentVO
        IPage<questionContentVO> resultPage = new Page<>();
        BeanUtils.copyProperties(questionList, resultPage);

        List<questionContentVO> voList = questionList.getRecords().stream()
                .map(this::convertContentToVO)
                .collect(Collectors.toList());

        resultPage.setRecords(voList);

        return resultPage;
    }

    private questionContentVO convertContentToVO(content content) {
        questionContentVO vo = new questionContentVO();
        vo.setId(content.getId());
        vo.setQuestionId(content.getQuestionId());
        vo.setAuthor(content.getAuthor());
        vo.setQuestionContent(content.getQuestionContent());
        vo.setAuthorId(content.getAuthorId());
        vo.setQuestionScore(content.getQuestionScore()); // 这里需要注意转换为 String
        vo.setQuestionType(content.getQuestionType()); // 转换题目类型
        vo.setCreateTime(content.getCreateTime());
        return vo;
    }

    public List<questionContentVO> getQuestionList1() throws StatusNotFoundException, IOException {
        SearchRequest searchRequest = new SearchRequest("content");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        String[] include = {"id", "questionId", "author", "authorId", "questionContent", "questionScore", "questionType", "createTime"};
        searchSourceBuilder.fetchSource(include, null);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        return Arrays.stream(searchResponse.getHits().getHits())
                .map(hit -> {
                    questionContentVO questionContentVO = new questionContentVO();
                    questionContentVO.setId((Integer) hit.getSourceAsMap().get("id"));
                    questionContentVO.setQuestionId((String) hit.getSourceAsMap().get("questionId"));
                    questionContentVO.setAuthor((String) hit.getSourceAsMap().get("author"));
                    questionContentVO.setAuthorId((String) hit.getSourceAsMap().get("authorId"));
                    questionContentVO.setQuestionContent((String) hit.getSourceAsMap().get("questionContent"));
                    questionContentVO.setQuestionScore((Integer) hit.getSourceAsMap().get("questionScore"));
                    questionContentVO.setQuestionType((Integer) hit.getSourceAsMap().get("questionType"));

                    Long createTimeMillis = (Long) hit.getSourceAsMap().get("createTime");
                    if (createTimeMillis != null) {
                        Instant instant = Instant.ofEpochMilli(createTimeMillis);
                        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                        questionContentVO.setCreateTime(localDateTime);
                    }
                    return questionContentVO;
                }).collect(Collectors.toList());
    }

    public org.springframework.data.domain.Page<questionContentVO> getQuestionListPage1(Integer limit, Integer currentPage, String keyword) throws StatusNotFoundException {
        // 设置默认值
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;

        Pageable pageable = PageRequest.of(currentPage - 1, limit);

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String trimmedKeyword = keyword.trim();
            boolQuery.must(QueryBuilders.multiMatchQuery(trimmedKeyword, "questionId", "author", "questionContent"));
        }
        org.springframework.data.domain.Page<content> search = contentRepository.search(boolQuery, pageable);

        List<questionContentVO> list = search.getContent().stream()
                .map(content -> {
                    questionContentVO questionContentVO = new questionContentVO();
                    questionContentVO.setId(content.getId());
                    questionContentVO.setQuestionId(content.getQuestionId());
                    questionContentVO.setAuthor(content.getAuthor());
                    questionContentVO.setAuthorId(content.getAuthorId());
                    questionContentVO.setQuestionContent(content.getQuestionContent());
                    questionContentVO.setQuestionScore(content.getQuestionScore());
                    questionContentVO.setQuestionType(content.getQuestionType());
                    questionContentVO.setCreateTime(content.getCreateTime());
                    return questionContentVO;
                }).collect(Collectors.toList());
        return new PageImpl<>(list, pageable, search.getTotalElements());
    }
}
