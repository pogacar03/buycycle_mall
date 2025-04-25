package com.example.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.exception.CustomException;
import com.example.service.AdminService;
import com.example.service.BusinessService;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 基础前端接口
 */
@RestController
public class WebController {

    @Resource
    private AdminService adminService;
    @Resource
    private BusinessService businessService;
    @Resource
    private UserService userService;

    @GetMapping("/")
    public Result hello() {
        return Result.success("访问成功");
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())) {
            return Result.error("400", "用户名或密码不能为空");
        }

        try {
            if (RoleEnum.ADMIN.name().equals(account.getRole())) {
                Account admin = adminService.login(account);
                return Result.success(admin);
            } else if (RoleEnum.BUSINESS.name().equals(account.getRole())) {
                Account business = businessService.login(account);
                return Result.success(business);
            } else if (RoleEnum.USER.name().equals(account.getRole())) {
                Account user = userService.login(account);
                return Result.success(user);
            }
            return Result.error("5007", "角色不存在");
        } catch (CustomException e) {
            // 直接传递业务异常
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            if (e.getMessage() != null) {
                if (e.getMessage().contains("用户不存在")) {
                    return Result.error("5004", "用户不存在");
                } else if (e.getMessage().contains("密码错误")) {
                    return Result.error("5003", "账号或密码错误");
                }
            }
            return Result.error("500", "登录失败：" + (e.getMessage() != null ? e.getMessage() : "未知错误"));
        }
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())) {
            return Result.error("400", "用户名或密码不能为空");
        }

        try {
            if (RoleEnum.ADMIN.name().equals(account.getRole())) {
                adminService.register(account);
            } else if (RoleEnum.BUSINESS.name().equals(account.getRole())) {
                businessService.register(account);
            } else if (RoleEnum.USER.name().equals(account.getRole())) {
                userService.register(account);
            } else {
                return Result.error("5007", "角色不存在");
            }
            return Result.success();
        } catch (CustomException e) {
            // 直接传递业务异常
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            if (e.getMessage() != null) {
                if (e.getMessage().contains("用户名已存在")) {
                    return Result.error("5001", "用户名已存在");
                }
            }
            return Result.error("500", "注册失败：" + (e.getMessage() != null ? e.getMessage() : "未知错误"));
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())
                || ObjectUtil.isEmpty(account.getNewPassword())) {
            return Result.error("4001", "参数缺失");
        }
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            adminService.updatePassword(account);
        }
        if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.updatePassword(account);
        }
        return Result.success();
    }

}