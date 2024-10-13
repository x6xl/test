package top.hcode.hoj.pojo.vo;

import lombok.Data;

/**
 * @Author: Himit_ZH
 * @Date: 2022/3/11 18:15
 * @Description:
 */
//用于封装检查用户名或邮箱是否存在的结果
@Data
public class CheckUsernameOrEmailVO {

    private Boolean email;

    private Boolean username;
}