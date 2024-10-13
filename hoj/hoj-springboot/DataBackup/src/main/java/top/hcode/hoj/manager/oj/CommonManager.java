package top.hcode.hoj.manager.oj;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import top.hcode.hoj.dao.problem.*;
import top.hcode.hoj.dao.training.TrainingCategoryEntityService;
import top.hcode.hoj.pojo.entity.problem.*;
import top.hcode.hoj.pojo.entity.training.TrainingCategory;
import top.hcode.hoj.pojo.vo.CaptchaVO;
import top.hcode.hoj.pojo.vo.ProblemTagVO;
import top.hcode.hoj.utils.RedisUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Himit_ZH
 * @Date: 2022/3/11 16:28
 * @Description:
 */
@Component
public class CommonManager {

    @Autowired
    private TagEntityService tagEntityService;

    @Autowired
    private TagClassificationEntityService tagClassificationEntityService;

    @Autowired
    private ProblemTagEntityService problemTagEntityService;

    @Autowired
    private LanguageEntityService languageEntityService;

    @Autowired
    private ProblemLanguageEntityService problemLanguageEntityService;

    @Autowired
    private RedisUtils redisUtil;

    @Autowired
    private ProblemEntityService problemEntityService;

    @Autowired
    private CodeTemplateEntityService codeTemplateEntityService;

    @Autowired
    private TrainingCategoryEntityService trainingCategoryEntityService;

    public CaptchaVO getCaptcha() {
        ArithmeticCaptcha specCaptcha = new ArithmeticCaptcha(90, 30, 4);
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);
        // 2位数运算
        specCaptcha.setLen(2);
        String verCode = specCaptcha.text().toLowerCase();//答案
        String key = IdUtil.simpleUUID();//验证码的唯一标识
        // 存入redis并设置过期时间为30分钟
        redisUtil.set(key, verCode, 1800);
        // 将key和base64返回给前端
        CaptchaVO captchaVo = new CaptchaVO();
        captchaVo.setImg(specCaptcha.toBase64());
        captchaVo.setCaptchaKey(key);
        return captchaVo;
    }


    public List<TrainingCategory> getTrainingCategory() {
        QueryWrapper<TrainingCategory> trainingCategoryQueryWrapper = new QueryWrapper<>();
        trainingCategoryQueryWrapper.isNull("gid");//查询小组id的条件为空，不属于任何小组的训练类别
        return trainingCategoryEntityService.list(trainingCategoryQueryWrapper);
    }

    public List<Tag> getAllProblemTagsList(String oj) {
        List<Tag> tagList;
        oj = oj.toUpperCase();
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.isNull("gid");
        if (oj.equals("ALL")) {
            tagList = tagEntityService.list(tagQueryWrapper);
        } else {
            tagQueryWrapper.eq("oj", oj);
            tagList = tagEntityService.list(tagQueryWrapper);
        }
        return tagList;
    }

    public List<ProblemTagVO> getProblemTagsAndClassification(String oj) {
        oj = oj.toUpperCase();
        List<ProblemTagVO> problemTagVOList = new ArrayList<>();
        List<TagClassification> classificationList = null;
        List<Tag> tagList = null;
        if (oj.equals("ALL")) {
            classificationList = tagClassificationEntityService.list();
            QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
            tagQueryWrapper.isNull("gid");
            tagList = tagEntityService.list(tagQueryWrapper);
        } else {
            QueryWrapper<TagClassification> tagClassificationQueryWrapper = new QueryWrapper<>();
            tagClassificationQueryWrapper.eq("oj", oj)
                    .orderByAsc("`rank`");//把问题分类按rank升序排序
            classificationList = tagClassificationEntityService.list(tagClassificationQueryWrapper);

            QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
            tagQueryWrapper.isNull("gid");
            tagQueryWrapper.eq("oj", oj);
            tagList = tagEntityService.list(tagQueryWrapper);
        }
        if (CollectionUtils.isEmpty(classificationList)) {//classificationList为空表示问题还没分类
            ProblemTagVO problemTagVo = new ProblemTagVO();
            problemTagVo.setTagList(tagList);
            problemTagVOList.add(problemTagVo);
        } else {
            for (TagClassification classification : classificationList) {
                ProblemTagVO problemTagVo = new ProblemTagVO();
                problemTagVo.setClassification(classification);
                List<Tag> tags = new ArrayList<>();
                if (!CollectionUtils.isEmpty(tagList)) {
                    Iterator<Tag> it = tagList.iterator();//iterator是一个接口，表示一个迭代器，可以遍历集合中的元素，提供hasNext和next等方法。
                    while (it.hasNext()) {//判断it变量是否有下一个元素
                        Tag tag = it.next();
                        if (classification.getId().equals(tag.getTcid())) {
                            tags.add(tag);
                            it.remove();
                        }
                    }
                }
                problemTagVo.setTagList(tags);
                problemTagVOList.add(problemTagVo);
            }
            if (tagList.size() > 0) {
                ProblemTagVO problemTagVo = new ProblemTagVO();
                problemTagVo.setTagList(tagList);
                problemTagVOList.add(problemTagVo);
            }
        }

        if (oj.equals("ALL")) {
            Collections.sort(problemTagVOList, problemTagVoComparator);//按照问题分类的排序字段升序排列
        }
        return problemTagVOList;
    }


    public Collection<Tag> getProblemTags(Long pid) {
        Map<String, Object> map = new HashMap<>();//键值对
        map.put("pid", pid);
        List<Long> tidList = problemTagEntityService.listByMap(map)
                .stream()//把问题标签的列表转换为一个流，表示一个元素的序列。Stream是一个接口，表示一个流，可以对元素进行各种操作，比如过滤，映射，排序等
                .map(ProblemTag::getTid)//表示对流中的每个元素，即每个问题标签，调用getTid方法，获取标签的id，返回一个新的流，表示标签id的序列。map是一个方法，用于对流中的元素进行映射，ProblemTag::getTid是一个方法引用，表示ProblemTag类的getTid方法
                .collect(Collectors.toList());//把标签收到一个列表中
        if (CollectionUtils.isEmpty(tidList)) {
            return new ArrayList<>();
        }
        return tagEntityService.listByIds(tidList);
    }


    public List<Language> getLanguages(Long pid, Boolean all) {

        String oj = "ME";
        if (pid != null) {
            Problem problem = problemEntityService.getById(pid);
            if (problem.getIsRemote()) {
                oj = problem.getProblemId().split("-")[0];
            }
        }

        if (oj.equals("GYM")) {  // GYM用与CF一样的编程语言列表
            oj = "CF";
        }

        QueryWrapper<Language> queryWrapper = new QueryWrapper<>();
        // 获取对应OJ支持的语言列表
        queryWrapper.eq(all != null && !all, "oj", oj);
        List<Language> languageList = languageEntityService.list(queryWrapper);
        return languageList.stream().sorted(Comparator.comparing(Language::getSeq, Comparator.reverseOrder())
                        .thenComparing(Language::getId))
                .collect(Collectors.toList());
    }

    public Collection<Language> getProblemLanguages(Long pid) {
        QueryWrapper<ProblemLanguage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid", pid).select("lid");
        List<Long> idList = problemLanguageEntityService.list(queryWrapper)
                .stream().map(ProblemLanguage::getLid).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(idList)){
            return Collections.emptyList();
        }
        Collection<Language> languages = languageEntityService.listByIds(idList);
        return languages.stream().sorted(Comparator.comparing(Language::getSeq, Comparator.reverseOrder())
                        .thenComparing(Language::getId))//按照语言的排序字段降序排列，如果排序字段相同，就按照语言的id升序排列，返回一个新的流，表示排序后的语言序列
                .collect(Collectors.toList());
    }

    public List<CodeTemplate> getProblemCodeTemplate(Long pid) {
        QueryWrapper<CodeTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid", pid);
        return codeTemplateEntityService.list(queryWrapper);
    }

    private Comparator<ProblemTagVO> problemTagVoComparator = (p1, p2) -> {//一个比较器，用于比较两个ProblemTagVO对象
        if (p1 == null) {
            return 1;
        }
        if (p2 == null) {
            return 1;
        }
        if (p1.getClassification() == null) {
            return 1;
        }
        if (p2.getClassification() == null) {
            return -1;
        }
        TagClassification p1Classification = p1.getClassification();
        TagClassification p2Classification = p2.getClassification();
        if (Objects.equals(p1Classification.getOj(), p2Classification.getOj())) {
            return p1Classification.getRank().compareTo(p2Classification.getRank());
        } else {
            if ("ME".equals(p1Classification.getOj())) {
                return -1;
            } else if ("ME".equals(p2Classification.getOj())) {
                return 1;
            } else {
                return p1Classification.getOj().compareTo(p2Classification.getOj());
            }
        }
    };
    //如果两个对象中有一个是空的，就返回1，表示第一个对象比较大。
    //如果两个对象中有一个的classification属性是空的，就返回1或-1，表示有classification的对象比较小。
    //如果两个对象的classification属性都不为空，就比较它们的oj和rank属性。
    //如果两个对象的oj属性相同，就比较它们的rank属性，rank小的对象比较小。
    //如果两个对象的oj属性不同，就比较它们的oj属性，按字典序排序，但是如果oj属性是"ME"，就表示它比较小。
}