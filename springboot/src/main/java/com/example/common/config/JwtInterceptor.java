package com.example.common.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.jwt.JWTUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.exception.CustomException;
import com.example.service.AdminService;
import com.example.service.BusinessService;
import com.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtInterceptor.class);

    @Resource
    private AdminService adminService;
    @Resource
    private BusinessService businessService;
    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        log.info("拦截到请求: {}", requestURI);

        // 检查是否为退货相关API，并是否包含businessId参数
        if (requestURI.startsWith("/return/")) {
            String businessIdParam = request.getParameter("businessId");
            if (businessIdParam != null && !businessIdParam.isEmpty()) {
                log.info("退货相关API请求携带了businessId参数: {}", businessIdParam);

                // 验证businessId对应的商家是否存在
                try {
                    Integer businessId = Integer.valueOf(businessIdParam);
                    Account business = businessService.selectById(businessId);
                    if (business != null) {
                        log.info("通过businessId验证商家信息成功");
                        // 将商家信息存入会话，后续流程可使用
                        request.getSession().setAttribute("user", business);
                        return true;
                    }
                } catch (Exception e) {
                    log.error("验证businessId失败", e);
                }
            }
        }

        // 1. 从http请求的header中获取token
        String token = request.getHeader(Constants.TOKEN);
        if (ObjectUtil.isEmpty(token)) {
            // 如果没拿到，从参数里再拿一次
            token = request.getParameter(Constants.TOKEN);
        }

        // 2. 开始执行认证
        if (ObjectUtil.isEmpty(token)) {
            log.warn("未提供token，认证失败");
            throw new CustomException(ResultCodeEnum.TOKEN_INVALID_ERROR);
        }

        Account account = null;
        try {
            // 解析token获取存储的数据
            String userRole = JWT.decode(token).getAudience().get(0);
            String userId = userRole.split("-")[0];
            String role = userRole.split("-")[1];
            log.info("从token解析出用户ID: {}, 角色: {}", userId, role);

            // 根据userId查询数据库
            if (RoleEnum.ADMIN.name().equals(role)) {
                account = adminService.selectById(Integer.valueOf(userId));
            }
            if (RoleEnum.BUSINESS.name().equals(role)) {
                account = businessService.selectById(Integer.valueOf(userId));
            }
            if (RoleEnum.USER.name().equals(role)) {
                account = userService.selectById(Integer.valueOf(userId));
            }

            // 如果正确获取了账户信息，存入会话
            if (account != null) {
                request.getSession().setAttribute("user", account);
                log.info("用户信息已存入会话: {}", account.getId());
            }
        } catch (Exception e) {
            log.error("解析token失败", e);
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
        }

        if (ObjectUtil.isNull(account)) {
            log.warn("用户不存在");
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }

        try {
            // 用户密码加签验证 token
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(account.getPassword())).build();
            jwtVerifier.verify(token); // 验证token
            log.info("token验证成功");
        } catch (JWTVerificationException e) {
            log.error("token验证失败", e);
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
        }

        return true;
    }
}