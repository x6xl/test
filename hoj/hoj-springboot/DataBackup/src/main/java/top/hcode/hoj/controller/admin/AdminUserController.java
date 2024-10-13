package top.hcode.hoj.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.dto.AdminEditUserDTO;
import top.hcode.hoj.pojo.vo.UserRolesVO;
import top.hcode.hoj.service.admin.user.AdminUserService;

import java.util.List;
import java.util.Map;


/**
 * @Author: Himit_ZH
 * @Date: 2020/12/6 15:18
 * @Description:
 */
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    //获取用户列表
    @GetMapping("/get-user-list")
    @RequiresAuthentication
    @RequiresPermissions("user_admin")
    public CommonResult<IPage<UserRolesVO>> getUserList(@RequestParam(value = "limit", required = false) Integer limit,
                                                        @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                        @RequestParam(value = "onlyAdmin", defaultValue = "false") Boolean onlyAdmin,
                                                        @RequestParam(value = "keyword", required = false) String keyword) {
        return adminUserService.getUserList(limit, currentPage, onlyAdmin, keyword);
    }

    //编辑用户信息
    @PutMapping("/edit-user")
    @RequiresPermissions("user_admin")
    @RequiresAuthentication
    public CommonResult<Void> editUser(@RequestBody AdminEditUserDTO adminEditUserDto) {
        return adminUserService.editUser(adminEditUserDto);
    }

    //删除用户
    @DeleteMapping("/delete-user")
    @RequiresPermissions("user_admin")
    @RequiresAuthentication
    public CommonResult<Void> deleteUser(@RequestBody Map<String, Object> params) {
        return adminUserService.deleteUser((List<String>) params.get("ids"));
    }

    //批量插入用户
    @PostMapping("/insert-batch-user")
    @RequiresPermissions("user_admin")
    @RequiresAuthentication
    public CommonResult<Void> insertBatchUser(@RequestBody Map<String, Object> params) {
        return adminUserService.insertBatchUser((List<List<String>>) params.get("users"));
    }

    //生成用户
    @PostMapping("/generate-user")
    @RequiresPermissions("user_admin")
    @RequiresAuthentication
    public CommonResult<Map<Object, Object>> generateUser(@RequestBody Map<String, Object> params) {
        return adminUserService.generateUser(params);
    }

}