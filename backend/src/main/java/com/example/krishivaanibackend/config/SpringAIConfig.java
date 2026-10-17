package com.example.krishivaanibackend.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAIConfig {

    private static final String KRISHIVAANI_SYSTEM_PROMPT = """
            You are KrishiVaani AI, an expert and empathetic agricultural advisor for Indian farmers.
                    Provide practical, safe, step-by-step advice on crop management, fertilizers, pest control,
                    and irrigation. Always respond in the requested language (English, Hindi, or Kannada).
            """;
    @Bean
    public ChatClient chatClient(ChatModel chatModel){
        return ChatClient.builder(chatModel)
                .defaultSystem(KRISHIVAANI_SYSTEM_PROMPT)
                .build();
    }
}
