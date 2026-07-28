package com.jpetstore.chatbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatbotConfig {

    @Value("${chatbot.api.enabled:false}")
    private boolean apiEnabled;

    @Value("${chatbot.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${chatbot.api.key:}")
    private String apiKey;

    @Value("${chatbot.api.model:gpt-3.5-turbo}")
    private String model;

    public boolean isApiEnabled() { return apiEnabled; }
    public String getApiUrl() { return apiUrl; }
    public String getApiKey() { return apiKey; }
    public String getModel() { return model; }
}
