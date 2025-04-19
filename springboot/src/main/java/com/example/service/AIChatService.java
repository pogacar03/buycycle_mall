package com.example.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.entity.Goods;
import com.example.mapper.GoodsMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIChatService {

    private static final Logger logger = LoggerFactory.getLogger(AIChatService.class);
    private static final String API_KEY = "sk-3811515de04741bcabfe186cfda2030d";
    private static final String API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    @Resource
    private GoodsMapper goodsMapper;

    private OkHttpClient client;

    @PostConstruct
    public void init() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        logger.info("AI服务初始化完成");
    }

    public Map<String, Object> processQuestion(String question) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查是否是价格范围查询
            logger.info("开始处理问题: {}", question);

            if (isPriceRangeQuery(question)) {
                logger.info("识别到价格范围查询: {}", question);
                return handlePriceRangeQuery(question);
            } else {
                logger.info("未识别为价格范围查询，使用常规对话处理");
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

            String answer = callAIApi(requestBody);

            result.put("code", 200);
            result.put("msg", "success");
            result.put("answer", answer);

            return result;

        } catch (Exception e) {
            logger.error("处理问题出错: {}", e.getMessage(), e);
            result.put("code", 500);
            result.put("msg", "error");
            result.put("answer", "抱歉，服务器出现错误：" + e.getMessage());
            return result;
        }
    }

    public Map<String, Object> test() {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-plus");

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", "你好");
            messages.add(message);

            requestBody.put("messages", messages);

            String response = callAIApi(requestBody);
            result.put("code", 200);
            result.put("msg", "success");
            result.put("data", response);

        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "error");
            result.put("data", e.getMessage());
        }
        return result;
    }

    /**
     * 检查是否在询问价格范围内的商品
     */
    // private boolean isPriceRangeQuery(String question) {
    // // 简化正则表达式，使其能更容易匹配价格范围
    // String regex = ".*?(\\d+)\\s*(到|至|-|~)\\s*(\\d+).*";
    // boolean matches = question.matches(regex);
    // logger.info("价格范围检测: '{}' 是否匹配: {}", question, matches);
    // return matches;
    // }

    private boolean isPriceRangeQuery(String question) {
        // 简化正则表达式，使其能更容易匹配价格范围
        String regex = ".*?(\\d+)\\s*(到|至|-|~)\\s*(\\d+).*";
        // 使用 Pattern.DOTALL 标志编译正则表达式
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(question);
        boolean matches = matcher.find();
        logger.info("价格范围检测: '{}' 是否匹配: {}", question, matches);
        return matches;
    }

    /**
     * 从问题中提取价格范围
     */
    private double[] extractPriceRange(String question) {
        double[] range = new double[2];
        range[0] = 0;
        range[1] = Double.MAX_VALUE;

        // 使用正则表达式提取价格范围
        String regex = "(\\d+)\\s*(到|至|-|~)\\s*(\\d+)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(question);

        if (matcher.find()) {
            try {
                range[0] = Double.parseDouble(matcher.group(1)); // 第一个价格
                range[1] = Double.parseDouble(matcher.group(3)); // 第二个价格
                logger.info("提取到价格范围: {} - {}", range[0], range[1]);
            } catch (NumberFormatException e) {
                logger.error("价格解析错误", e);
            }
        } else {
            logger.warn("未能从问题中提取价格范围: {}", question);
        }

        return range;
    }

    private Map<String, Object> handlePriceRangeQuery(String question) {
        try {
            // 1. 提取价格范围
            double[] priceRange = extractPriceRange(question);
            double minPrice = priceRange[0];
            double maxPrice = priceRange[1];

            // 2. 查询数据库
            List<Goods> goodsList = goodsMapper.selectByPriceRange(minPrice, maxPrice);
            logger.info("查询到{}个价格在{}-{}范围内的商品", goodsList.size(), minPrice, maxPrice);

            // 3. 构建提示词
            StringBuilder prompt = new StringBuilder();
            prompt.append("你是BuyCycle购物平台的AI客服助手。用户询问价格在").append(minPrice)
                    .append("到").append(maxPrice).append("元之间的商品。\n\n");

            if (goodsList.isEmpty()) {
                prompt.append("很抱歉，我们目前没有价格在这个范围内的商品。请问您有其他价格区间的需求吗？");
            } else {
                prompt.append("以下是我们平台上价格在这个范围内的商品：<br><br>");

                for (Goods goods : goodsList) {
                    prompt.append("<div style='margin-bottom: 20px;'>");
                    prompt.append("<strong>名称：</strong>").append(goods.getName()).append("<br>");
                    prompt.append("<strong>价格：</strong>").append(goods.getPrice()).append("元<br>");
                    prompt.append("<strong>描述：</strong>").append(goods.getDescription()).append("<br>");
                    prompt.append("<a href='http://localhost:8080/front/detail?id=").append(goods.getId())
                            .append("' target='_blank'>"); // 使用商品ID构建链接
                    prompt.append("<img src='").append(goods.getImg()).append("' alt='").append(goods.getName())
                            .append("' style='width: 100px; height: auto;'><br>");
                    prompt.append("点击查看商品</a>");
                    prompt.append("</div><br>");
                }

                prompt.append("请根据用户的问题，推荐合适的商品，并解释推荐理由。");
            }

            // 4. 调用AI服务
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-plus");

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", prompt.toString());
            messages.add(systemMessage);

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", question);
            messages.add(userMessage);

            requestBody.put("messages", messages);

            String answer = callAIApi(requestBody);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("msg", "success");
            result.put("answer", answer);

            return result;

        } catch (Exception e) {
            logger.error("处理价格范围查询出错: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("msg", "error");
            result.put("answer", "抱歉，在查询价格范围内的商品时出现错误：" + e.getMessage());
            return result;
        }
    }

    private String callAIApi(Map<String, Object> requestBody) throws IOException {
        String jsonBody = JSON.toJSONString(requestBody);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonBody, JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        logger.info("开始调用AI服务");
        long startTime = System.currentTimeMillis();

        try (Response response = client.newCall(request).execute()) {
            long endTime = System.currentTimeMillis();
            logger.info("AI服务调用耗时: {}ms", (endTime - startTime));

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

            return jsonResponse
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        } catch (IOException e) {
            logger.error("AI服务调用失败: {}", e.getMessage(), e);
            throw e;
        }
    }
}