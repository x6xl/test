package top.hcode.hoj.manager.question;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusForbiddenException;
import top.hcode.hoj.common.exception.StatusNotFoundException;
import top.hcode.hoj.dao.question.contentEntityService;
import top.hcode.hoj.dao.question.optionsEntityService;
import top.hcode.hoj.mapper.questionContentMapper;
import top.hcode.hoj.pojo.entity.question.content;
import top.hcode.hoj.pojo.entity.question.options;
import top.hcode.hoj.shiro.AccountProfile;

import java.util.List;

@Component
public class optionsManager {
    @Autowired
    optionsEntityService optionsEntityService;
    @Autowired
    contentEntityService contentEntityService;
    @Autowired
    questionContentMapper questionContentMapper;

    public List<options> getOptionsByQid(String questionId) throws StatusNotFoundException, StatusFailException {

        QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper
                .eq("question_id", questionId);

        content content = contentEntityService.getOne(contentQueryWrapper);
        if (content == null) {
            throw new StatusNotFoundException("查询失败，该题目已不存在！");
        }

        QueryWrapper<options> optionsQueryWrapper1 = new QueryWrapper<>();
        optionsQueryWrapper1
                .select("id")
                .eq("question_id", questionId);

        List<options> list1 = optionsEntityService.list(optionsQueryWrapper1);
        if (list1 == null) {
            throw new StatusNotFoundException("查询失败，该选项已不存在！");
        }


        content content1 = questionContentMapper.getQuestion_contentByQid(questionId);
        if (content1.getQuestionType() > 2) {
            throw new StatusFailException("查询选项失败，该题目不是选择题！");
        }

        List<options> list = optionsEntityService.getOptionsByQid(questionId);
        return list;
    }

    public void addOptions(options options) throws StatusForbiddenException, StatusFailException, StatusNotFoundException {

        QueryWrapper<content> contentQueryWrapper1 = new QueryWrapper<>();
        contentQueryWrapper1
                .eq("question_id", options.getQuestionId());

        content content = contentEntityService.getOne(contentQueryWrapper1);
        if (content == null) {
            throw new StatusNotFoundException("添加选项失败，该题目已不存在！");
        }

        QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper
                .select("questionType")
                .eq("questionId", options.getQuestionId());

        content content1 = contentEntityService.getOne(contentQueryWrapper);
        if (content1.getQuestionType() > 2) {
            throw new StatusFailException("添加选项失败，该题目不是选择题！");
        }

//        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
//
//        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
//        if (!isRoot
//                && !options.getAuthorId().equals(userRolesVo.getUid())) {
//            throw new StatusForbiddenException("对不起，您无权限操作！");
//        }

        boolean isOk = optionsEntityService.saveOrUpdate(options);
        if (!isOk) {
            throw new StatusFailException("添加选项失败，请重新尝试！");
        }

    }

    public void removeOptionsById(int id) throws StatusForbiddenException, StatusFailException, StatusNotFoundException {


        QueryWrapper<options> optionsQueryWrapper = new QueryWrapper<>();
        optionsQueryWrapper
                .eq("id", id);

        options options1 = optionsEntityService.getOne(optionsQueryWrapper);
        if (options1 == null) {
            throw new StatusNotFoundException("删除失败，该选项已不存在！");
        }

        options options = optionsEntityService.getOne(optionsQueryWrapper);

        QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper
                .eq("question_id", options.getQuestionId());

        content content = contentEntityService.getOne(contentQueryWrapper);
        if (content == null) {
            throw new StatusNotFoundException("查询失败，该题目已不存在！");
        }
//        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
//
//        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
//        if (!isRoot
//                && !options.getAuthorId().equals(userRolesVo.getUid())) {
//            throw new StatusForbiddenException("对不起，您无权限操作！");
//        }

        boolean isOk = optionsEntityService.removeById(id);
        if (!isOk) {
            throw new StatusFailException("删除失败，请重新尝试！");
        }


    }

    public void updateOptionsById(options options) throws StatusForbiddenException, StatusFailException, StatusNotFoundException {

        QueryWrapper<content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper
                .eq("question_id", options.getQuestionId());

        content content = contentEntityService.getOne(contentQueryWrapper);
        if (content == null) {
            throw new StatusNotFoundException("修改失败，该题目已不存在！");
        }

        QueryWrapper<options> optionsQueryWrapper1 = new QueryWrapper<>();
        optionsQueryWrapper1
                .eq("id", options.getId());
        options options2 = optionsEntityService.getOne(optionsQueryWrapper1);
        if (options2 == null) {
            throw new StatusNotFoundException("修改失败，该选项已不存在！");
        }

//        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
//
//        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
//        if (!isRoot
//                && !options.getAuthorId().equals(userRolesVo.getUid())) {
//            throw new StatusForbiddenException("对不起，您无权限操作！");
//        }

        UpdateWrapper<options> optionsUpdateWrapper = new UpdateWrapper<>();
        optionsUpdateWrapper.set("question_id", options.getQuestionId())
                .set("option_content", options.getOptionContent())
                .set("author_id", options.getAuthorId())
                .set("author", options.getAuthor())
                .set("option_content", options.getOptionContent())
                .eq("id", options.getId());

        boolean isOk = optionsEntityService.update(optionsUpdateWrapper);
        if (!isOk) {
            throw new StatusFailException("更新选项失败，请重新尝试！");
        }
    }
}
