package top.hcode.hoj.controller.group;

import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.entity.common.Announcement;
import top.hcode.hoj.pojo.vo.AnnouncementVO;
import top.hcode.hoj.service.group.announcement.GroupAnnouncementService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: LengYun
 * @Date: 2022/3/11 13:36
 * @Description:
 */
//团队公告
@RestController
@RequiresAuthentication
@RequestMapping("/api/group")
public class GroupAnnouncementController {

    @Autowired
    private GroupAnnouncementService groupAnnouncementService;
//获取团队公告列表
    @GetMapping("/get-announcement-list")
    public CommonResult<IPage<AnnouncementVO>> getAnnouncementList(@RequestParam(value = "limit", required = false) Integer limit,
                                                                   @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                   @RequestParam(value = "gid", required = true) Long gid) {
        return groupAnnouncementService.getAnnouncementList(limit, currentPage, gid);
    }
//获取管理员团队公告列表
    @GetMapping("/get-admin-announcement-list")
    public CommonResult<IPage<AnnouncementVO>> getAdminAnnouncementList(@RequestParam(value = "limit", required = false) Integer limit,
                                                                        @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                        @RequestParam(value = "gid", required = true) Long gid) {
        return groupAnnouncementService.getAdminAnnouncementList(limit, currentPage, gid);
    }
    //添加团队公告列表
    @PostMapping("/announcement")
    public CommonResult<Void> addAnnouncement(@RequestBody Announcement announcement) {
        return groupAnnouncementService.addAnnouncement(announcement);
    }
    //更新团队公告列表
    @PutMapping("/announcement")
    public CommonResult<Void> updateAnnouncement(@RequestBody Announcement announcement) {
        return groupAnnouncementService.updateAnnouncement(announcement);
    }
    //删除团队公告列表
    @DeleteMapping("/announcement")
    public CommonResult<Void> deleteAnnouncement(@RequestParam(value = "aid", required = true) Long aid) {
        return groupAnnouncementService.deleteAnnouncement(aid);
    }

}
