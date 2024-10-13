package top.hcode.hoj.controller.group;

import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.entity.group.GroupMember;
import top.hcode.hoj.pojo.vo.GroupMemberVO;
import top.hcode.hoj.service.group.member.GroupMemberService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: LengYun
 * @Date: 2022/3/11 13:36
 * @Description:
 */
//团队成员管理
@RestController
@RequiresAuthentication
@RequestMapping("/api/group")
public class GroupMemberController {

    @Autowired
    private GroupMemberService groupMemberService;
//获取团队成员列表
    @GetMapping("/get-member-list")
    public CommonResult<IPage<GroupMemberVO>> getMemberList(@RequestParam(value = "limit", required = false) Integer limit,
                                                            @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                            @RequestParam(value = "keyword", required = false) String keyword,
                                                            @RequestParam(value = "auth", required = false) Integer auth,
                                                            @RequestParam(value = "gid", required = true) Long gid) {
        return groupMemberService.getMemberList(limit, currentPage, keyword, auth, gid);
    }
//获取团队的申请列表
    @GetMapping("/get-apply-list")
    public CommonResult<IPage<GroupMemberVO>> getApplyList(@RequestParam(value = "limit", required = false) Integer limit,
                                                           @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                           @RequestParam(value = "keyword", required = false) String keyword,
                                                           @RequestParam(value = "auth", required = false) Integer auth,
                                                           @RequestParam(value = "gid", required = true) Long gid) {
        return groupMemberService.getApplyList(limit, currentPage, keyword, auth, gid);
    }

    @PostMapping("/member")
    @RequiresAuthentication
    public CommonResult<Void> addGroupMember(@RequestParam(value = "gid", required = true) Long gid,
                                             @RequestParam(value = "code", required = false) String code,
                                             @RequestParam(value = "reason", required = false) String reason) {
        return groupMemberService.addMember(gid, code, reason);
    }

    @PutMapping("/member")
    @RequiresAuthentication
    public CommonResult<Void> updateMember(@RequestBody GroupMember groupMember) {
        return groupMemberService.updateMember(groupMember);
    }
//删除某个团队成员
    @DeleteMapping("/member")
    @RequiresAuthentication
    public CommonResult<Void> deleteMember(@RequestParam(value = "uid", required = true) String uid,
                                           @RequestParam(value = "gid", required = true) Long gid) {
        return groupMemberService.deleteMember(uid, gid);
    }
//退出团队
    @DeleteMapping("/member/exit")
    @RequiresAuthentication
    public CommonResult<Void> exitGroup(@RequestParam(value = "gid", required = true) Long gid) {
        return groupMemberService.exitGroup(gid);
    }


}
