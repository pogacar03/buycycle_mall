package com.example.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebConfig implements  WebMvcConfigurer {

    @Resource
    private JwtInterceptor jwtInterceptor;

    // 加自定义拦截器JwtInterceptor，设置拦截规则
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/")
                .excludePathPatterns("/login")
                .excludePathPatterns("/register")
                .excludePathPatterns("/files/**")
               .excludePathPatterns("/type/**")
                .excludePathPatterns("/goods/**")
                .excludePathPatterns("/notice/selectAll")
                .excludePathPatterns("/goods/**")
                .excludePathPatterns("/api/alipay/**")
                .excludePathPatterns("/comment/selectByGoodsId/**")
                .excludePathPatterns("/ai/test")// 添加AI测试接口到白名单
                .excludePathPatterns("/ai/ask")// 添加AI问答接口到白名单
                .excludePathPatterns("/return/page") // 临时允许直接访问，依赖于参数中的businessId
                .excludePathPatterns("/front/payment/success")
        ;
    }
}