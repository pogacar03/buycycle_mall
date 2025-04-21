package com.example.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.response.AlipayTradePagePayResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class AlipayService {
    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private OrdersService ordersService;

    @Value("${alipay.returnUrl}")
    private String returnUrl;

    @Value("${alipay.notifyUrl}")
    private String notifyUrl;

    @Value("${alipay.publicKey}")
    private String alipayPublicKey;

    /**
     * 创建支付表单
     */
    public String createPayment(String orderId, BigDecimal amount, String subject) throws AlipayApiException {
        return createPayment(orderId, amount, subject, null);
    }

    /**
     * 创建支付表单 - 带自定义returnUrl
     */
    public String createPayment(String orderId, BigDecimal amount, String subject, String customReturnUrl)
            throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();

        // 设置默认回调地址
        String actualReturnUrl = returnUrl;

        // 如果提供了自定义回调地址，添加custom_return_url参数
        if (customReturnUrl != null && !customReturnUrl.isEmpty()) {
            // 将自定义回调地址作为参数添加到系统回调URL中
            actualReturnUrl = returnUrl + (returnUrl.contains("?") ? "&" : "?") + "custom_return_url="
                    + customReturnUrl;
        }

        request.setReturnUrl(actualReturnUrl);
        request.setNotifyUrl(notifyUrl);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", amount.toString());
        bizContent.put("subject", subject);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

        request.setBizContent(bizContent.toString());

        AlipayTradePagePayResponse alipayTradePagePayResponse = alipayClient.pageExecute(request);

        return alipayTradePagePayResponse.getBody();
    }

    /**
     * 验证支付宝异步通知
     */
    public boolean verifyPayment(Map<String, String> params) throws AlipayApiException {
        return AlipaySignature.rsaCheckV1(params, alipayPublicKey, "UTF-8", "RSA2");
    }

    /**
     * 处理支付成功的订单
     */
    public boolean handlePaymentSuccess(String orderId) {
        return ordersService.updatePayStatus(orderId, "已支付");
    }
}