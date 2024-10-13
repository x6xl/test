package top.hcode.hoj.validator;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.pojo.entity.problem.Problem;
import top.hcode.hoj.pojo.entity.question.content;
import top.hcode.hoj.pojo.entity.question.options;

import javax.annotation.Resource;

@Component
public class questionValidator {
    @Resource
    private CommonValidator commonValidator;

    public void validateProblem(content content) throws StatusFailException {
       if (content==null){
           throw new StatusFailException("题目的配置项不能为空！");
       }
       if (StrUtil.isBlank(content.getQuestionId())) {
            throw new StatusFailException("题目的展示ID不能为空！");
       }

    }

    public void validateoptions(options options) throws StatusFailException{
        if (options==null){
            throw new StatusFailException("题目选项的配置项不能为空！");
        }
    }

    public void validateProblemUpdate(content content) throws StatusFailException{
        if (content==null){
            throw new StatusFailException("题目的配置项不能为空！");
        }
        if (content.getQuestionId()==null) {
            throw new StatusFailException("题目的展示ID不能为空！");
        }
        validateProblem(content);
    }
}
