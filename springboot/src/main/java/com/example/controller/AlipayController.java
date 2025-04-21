package com.example.controller;

import com.example.common.Result;
import com.example.entity.PaymentInfo;
import com.example.service.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/alipay")
public class AlipayController {
    @Autowired
    private AlipayService alipayService;

    /**
     * 创建支付
     */
    @PostMapping("/pay")
    public Result pay(@RequestBody PaymentInfo paymentInfo) {
        try {
            String form = alipayService.createPayment(
                    paymentInfo.getOrderId(),
                    paymentInfo.getAmount(),
                    paymentInfo.getSubject(),
                    paymentInfo.getReturnUrl());
            return Result.success(form);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("500", "创建支付失败: " + e.getMessage());
        }
    }

    /**
     * 支付宝异步通知处理
     */
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        Map<String, String> params = convertRequestToMap(request);
        try {
            // 验证签名
            if (alipayService.verifyPayment(params)) {
                // 获取交易状态
                String orderId = params.get("out_trade_no");
                String tradeStatus = params.get("trade_status");

                // 交易成功
                if ("TRADE_SUCCESS".equals(tradeStatus)) {
                    // 更新订单状态为已支付
                    alipayService.handlePaymentSuccess(orderId);
                    System.out.println("订单 " + orderId + " 支付成功");
                }
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "fail";
    }

    /**
     * 支付宝同步回调处理
     */
    @GetMapping("/return")
    public void returnUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // 获取订单号和自定义返回地址
            String orderId = request.getParameter("out_trade_no");
            String customReturnUrl = request.getParameter("custom_return_url");

            // 如果有自定义返回地址，优先使用
            if (customReturnUrl != null && !customReturnUrl.isEmpty()) {
                // 在URL中添加订单ID参数
                response.sendRedirect(
                        customReturnUrl + (customReturnUrl.contains("?") ? "&" : "?") + "orderId=" + orderId);
            } else {
                // 否则使用默认的支付成功页面
                response.sendRedirect("/front/payment/success?orderId=" + orderId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("/front/orders");
        }
    }

    /**
     * 将HttpServletRequest转换为Map
     */
    private Map<String, String> convertRequestToMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }
}