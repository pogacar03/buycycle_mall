package com.example.controller;

import com.example.service.AIChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@CrossOrigin
public class AIChatController {

    private static final Logger logger = LoggerFactory.getLogger(AIChatController.class);

    @Resource
    private AIChatService aiChatService;

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

            return aiChatService.processQuestion(question);

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
        return aiChatService.test();
    }
}