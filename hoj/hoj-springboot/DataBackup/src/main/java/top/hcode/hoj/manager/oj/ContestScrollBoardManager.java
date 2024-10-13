package top.hcode.hoj.manager.oj;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.dao.contest.ContestEntityService;
import top.hcode.hoj.dao.contest.ContestProblemEntityService;
import top.hcode.hoj.dao.judge.JudgeEntityService;
import top.hcode.hoj.dao.problem.ProblemEntityService;
import top.hcode.hoj.pojo.entity.contest.Contest;
import top.hcode.hoj.pojo.entity.contest.ContestProblem;
import top.hcode.hoj.pojo.entity.problem.Problem;
import top.hcode.hoj.pojo.vo.ContestScrollBoardInfoVO;
import top.hcode.hoj.pojo.vo.ContestScrollBoardSubmissionVO;
import top.hcode.hoj.utils.Constants;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author Himit_ZH
 * @Date 2022/10/3
 */
@Component
public class ContestScrollBoardManager {

    @Resource
    private ContestEntityService contestEntityService;

    @Resource
    private ContestProblemEntityService contestProblemEntityService;

    @Resource
    private JudgeEntityService judgeEntityService;

    @Resource
    private ProblemEntityService problemEntityService;

    @Resource
    private ContestCalculateRankManager contestCalculateRankManager;


    public ContestScrollBoardInfoVO getContestScrollBoardInfo(Long cid) throws StatusFailException {
        Contest contest = contestEntityService.getById(cid);
        if (contest == null) {
            throw new StatusFailException("比赛不存在 (The contest does not exist)");
        }

        if (!Objects.equals(contest.getType(), Constants.Contest.TYPE_ACM.getCode())) {
            throw new StatusFailException("非ACM赛制的比赛无法进行滚榜  (Non - ACM contest board cannot be rolled)");
        }

        if (!contest.getSealRank()) {
            throw new StatusFailException("比赛未开启封榜，无法进行滚榜 (The contest has not been closed, and cannot roll)");
        }

        if (!Objects.equals(contest.getStatus(), Constants.Contest.STATUS_ENDED.getCode())) {
            throw new StatusFailException("比赛未结束，禁止进行滚榜 (Roll off is prohibited before the contest is over)");
        }

        QueryWrapper<ContestProblem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", cid);
        List<ContestProblem> contestProblemList = contestProblemEntityService.list(queryWrapper);

        List<Long> pidList = contestProblemList.stream().map(ContestProblem::getPid).collect(Collectors.toList());//储存比赛题目的id
        if (!CollectionUtils.isEmpty(pidList)) {
            QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
            problemQueryWrapper.select("id", "auth")
                    .ne("auth", 2)
                    .in("id", pidList);
            List<Problem> problemList = problemEntityService.list(problemQueryWrapper);
            List<Long> idList = problemList.stream().map(Problem::getId).collect(Collectors.toList());
            contestProblemList = contestProblemList.stream()
                    .filter(p -> idList.contains(p.getPid()))
                    .collect(Collectors.toList());//根据idList中是否包含其pid属性，进行过滤,去除不可见的比赛题目
        }

        HashMap<String, String> balloonColor = new HashMap<>();
        for (ContestProblem contestProblem : contestProblemList) {
            balloonColor.put(contestProblem.getDisplayId(), contestProblem.getColor());
        }

        ContestScrollBoardInfoVO info = new ContestScrollBoardInfoVO();
        info.setId(cid);
        info.setProblemCount(contestProblemList.size());
        info.setBalloonColor(balloonColor);
        info.setRankShowName(contest.getRankShowName());
        info.setStarUserList(starAccountToList(contest.getStarAccount()));
        info.setStartTime(contest.getStartTime());
        info.setSealRankTime(contest.getSealRankTime());

        return info;
    }

    private List<String> starAccountToList(String starAccountStr) {
        if (StringUtils.isEmpty(starAccountStr)) {
            return new ArrayList<>();
        }
        JSONObject jsonObject = JSONUtil.parseObj(starAccountStr);
        List<String> list = jsonObject.get("star_account", List.class);
        return list;
    }


    public List<ContestScrollBoardSubmissionVO> getContestScrollBoardSubmission(Long cid, Boolean removeStar) throws StatusFailException {
        Contest contest = contestEntityService.getById(cid);
        if (contest == null) {
            throw new StatusFailException("比赛不存在 (The contest does not exist)");
        }

        if (!Objects.equals(contest.getType(), Constants.Contest.TYPE_ACM.getCode())) {
            throw new StatusFailException("非ACM赛制的比赛无法进行滚榜  (Non - ACM contest board cannot be rolled)");
        }

        if (!contest.getSealRank()) {
            throw new StatusFailException("比赛未开启封榜，无法进行滚榜 (The contest has not been closed, and cannot roll)");
        }

        if (!Objects.equals(contest.getStatus(), Constants.Contest.STATUS_ENDED.getCode())) {
            throw new StatusFailException("比赛未结束，禁止进行滚榜 (Roll off is prohibited before the contest is over)");
        }

        List<String> removeUidList = contestCalculateRankManager.getSuperAdminUidList(contest.getGid());
        if (!removeUidList.contains(contest.getUid())) {
            removeUidList.add(contest.getUid());
        }
        List<ContestScrollBoardSubmissionVO> submissions = judgeEntityService.getContestScrollBoardSubmission(cid, removeUidList);
        if (removeStar && StrUtil.isNotBlank(contest.getStarAccount())) {
            JSONObject jsonObject = JSONUtil.parseObj(contest.getStarAccount());//将contest的starAccount属性转换为一个JSONObject对象
            List<String> usernameList = jsonObject.get("star_account", List.class);
            if (!CollectionUtils.isEmpty(usernameList)) {
                submissions = submissions.stream()
                        .filter(submission -> !usernameList.contains(submission.getUsername()))
                        .collect(Collectors.toList());
            }//去除星号用户的提交记录
        }
        submissions = submissions.stream()
                .filter(submission -> submission.getSubmitTime().getTime() < contest.getEndTime().getTime())
                .collect(Collectors.toList());
        return submissions;
    }
}
