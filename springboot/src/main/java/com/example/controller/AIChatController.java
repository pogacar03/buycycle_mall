package com.example.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/ai")
@CrossOrigin
public class AIChatController {

    private static final Logger logger = LoggerFactory.getLogger(AIChatController.class);
    private static final String API_KEY = "sk-3811515de04741bcabfe186cfda2030d";
    private static final String API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;

    public AIChatController() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        logger.info("AI服务初始化完成");
    }

    @PostMapping("/ask")
    public Map<String, Object> ask(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            String question = params.get("question");
            logger.info("收到问题: {}", question);

            if (question == null || question.trim().isEmpty()) {
                result.put("code", 400);
                result.put("msg", "问题不能为空");
                result.put("answer", "请输入您的问题");
                return result;
            }

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-plus");

            List<Map<String, String>> messages = new ArrayList<>();

            // 添加系统角色消息
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是BuyCycle购物平台的AI客服助手，请帮助用户解答问题。");
            messages.add(systemMessage);

            // 添加用户问题
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", question);
            messages.add(userMessage);

            requestBody.put("messages", messages);

            // 将Map转换为JSON字符串
            String jsonBody = JSON.toJSONString(requestBody);

            // 构建请求体
            okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonBody, JSON_MEDIA_TYPE);

            // 构建请求
            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build();

            logger.info("开始调用AI服务");

            // 发送请求
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("请求失败: " + response.code());
                }

                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    throw new IOException("响应体为空");
                }

                String responseString = responseBody.string();
                logger.info("AI响应: {}", responseString);

                JSONObject jsonResponse = JSON.parseObject(responseString);

                // 解析响应
                String answer = jsonResponse
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                result.put("code", 200);
                result.put("msg", "success");
                result.put("answer", answer);
            }

            return result;

        } catch (Exception e) {
            logger.error("处理请求出错: {}", e.getMessage(), e);
            result.put("code", 500);
            result.put("msg", "error");
            result.put("answer", "抱歉，服务器出现错误：" + e.getMessage());
            return result;
        }
    }

    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 构建一个简单的测试请求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-plus");

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", "你好");
            messages.add(message);

            requestBody.put("messages", messages);

            String jsonBody = JSON.toJSONString(requestBody);
            okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonBody, JSON_MEDIA_TYPE);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                result.put("code", response.code());
                result.put("msg", response.isSuccessful() ? "success" : "error");
                ResponseBody responseBody = response.body();
                result.put("data", responseBody != null ? responseBody.string() : "无响应数据");
            }

        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "error");
            result.put("data", e.getMessage());
        }
        return result;
    }
}