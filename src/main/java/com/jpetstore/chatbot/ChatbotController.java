package com.jpetstore.chatbot;

import com.jpetstore.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    private static final String HISTORY_KEY = "chat_history";

    @PostMapping("/message")
    public Result<ChatMessage> sendMessage(@RequestParam String message, HttpSession session) {
        if (message == null || message.trim().isEmpty()) {
            return Result.badRequest("消息不能为空哦~");
        }

        // Get or create chat history
        @SuppressWarnings("unchecked")
        List<ChatMessage> history = (List<ChatMessage>) session.getAttribute(HISTORY_KEY);
        if (history == null) {
            history = new ArrayList<>();
            session.setAttribute(HISTORY_KEY, history);
        }

        // Add user message to history
        ChatMessage userMsg = new ChatMessage("user", message.trim());
        history.add(userMsg);

        // Keep only last 20 messages to prevent session bloat
        if (history.size() > 20) {
            history = history.subList(history.size() - 20, history.size());
        }

        // Generate response
        ChatMessage response = chatbotService.generateResponse(message, history);
        history.add(response);

        session.setAttribute(HISTORY_KEY, history);
        return Result.success(response);
    }

    @GetMapping("/history")
    public Result<List<ChatMessage>> getHistory(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<ChatMessage> history = (List<ChatMessage>) session.getAttribute(HISTORY_KEY);
        if (history == null) {
            history = new ArrayList<>();
        }
        return Result.success(history);
    }

    @PostMapping("/clear")
    public Result<String> clearHistory(HttpSession session) {
        session.removeAttribute(HISTORY_KEY);
        return Result.success("聊天记录已清空");
    }
}
