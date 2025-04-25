package com.example.exception;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages = "com.example.controller")
public class GlobalExceptionHandler {

    private static final Log log = LogFactory.get();

    // 统一异常处理@ExceptionHandler,主要用于Exception
    @ExceptionHandler(Exception.class)
    @ResponseBody // 返回json串
    public Result error(HttpServletRequest request, Exception e) {
        log.error("异常信息：", e);

        // 处理空指针异常
        if (e instanceof NullPointerException) {
            // 记录详细错误信息
            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace.length > 0) {
                StackTraceElement element = stackTrace[0];
                String errorLocation = element.getClassName() + ":" + element.getMethodName() + "("
                        + element.getLineNumber() + ")";
                log.error("空指针异常发生位置: {}", errorLocation);
            }
            return Result.error("500", "系统错误：空指针异常");
        }

        return Result.error("500", "系统异常，请稍后重试");
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody // 返回json串
    public Result customError(HttpServletRequest request, CustomException e) {
        log.error("业务异常：" + e.getCode() + " - " + e.getMsg());
        return Result.error(e.getCode(), e.getMsg());
    }
}
