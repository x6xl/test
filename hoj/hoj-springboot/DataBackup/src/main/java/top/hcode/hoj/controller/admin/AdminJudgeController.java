package top.hcode.hoj.controller.admin;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.entity.judge.Judge;
import top.hcode.hoj.service.admin.rejudge.RejudgeService;


import javax.annotation.Resource;

/**
 * @Author: Himit_ZH
 * @Date: 2021/1/3 14:09
 * @Description: 超管重判提交
 */

//重新评判、取消评判、手动评判
@RestController
@RequestMapping("/api/admin/judge")
public class AdminJudgeController {

    @Resource
    private RejudgeService rejudgeService;

    //重新评判一个提交的结果
    @GetMapping("/rejudge")
    @RequiresAuthentication
    @RequiresRoles("root")  // 只有超级管理员能操作
    @RequiresPermissions("rejudge")
    public CommonResult<Judge> rejudge(@RequestParam("submitId") Long submitId) {
        return rejudgeService.rejudge(submitId);
    }

    //重新评判一个比赛中的问题
    @GetMapping("/rejudge-contest-problem")
    @RequiresAuthentication
    @RequiresRoles("root")  // 只有超级管理员能操作
    @RequiresPermissions("rejudge")
    public CommonResult<Void> rejudgeContestProblem(@RequestParam("cid") Long cid, @RequestParam("pid") Long pid) {
        return rejudgeService.rejudgeContestProblem(cid, pid);
    }

    //手动评判一个提交的结果
    @GetMapping("/manual-judge")
    @RequiresAuthentication
    @RequiresRoles("root")  // 只有超级管理员能操作
    @RequiresPermissions("rejudge")
    public CommonResult<Judge> manualJudge(@RequestParam("submitId") Long submitId,
                                           @RequestParam("status") Integer status,
                                           @RequestParam(value = "score", required = false) Integer score) {
        return rejudgeService.manualJudge(submitId, status, score);
    }

    //取消一个提交的评判
    @GetMapping("/cancel-judge")
    @RequiresAuthentication
    @RequiresRoles("root")  // 只有超级管理员能操作
    @RequiresPermissions("rejudge")
    public CommonResult<Judge> cancelJudge(@RequestParam("submitId") Long submitId) {
        return rejudgeService.cancelJudge(submitId);
    }
}
