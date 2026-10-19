package com.example.krishivaanibackend.dto;

import com.example.krishivaanibackend.entity.Question;

import java.time.LocalDateTime;

public class ChatResponseDTO {

    private Long id;
    private String content;
    private String language;
    private String answer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ChatResponseDTO() {}

    public static ChatResponseDTO from(Question question){
        ChatResponseDTO dto = new ChatResponseDTO();
        dto.id = question.getId();
        dto.content =question.getContent();
        dto.language = question.getLanguage();
        dto.answer = question.getAnswer();
        dto.createdAt = question.getCreatedAt();
        dto.updatedAt = question.getUpdatedAt();
        return dto;
    }

    public static ChatResponseDTO to(Long id, String content, String language, String answer) {

        ChatResponseDTO dto = new ChatResponseDTO();
        dto.id = id;
        dto.content = content;
        dto.language = language;
        dto.answer = answer;
        dto.createdAt = LocalDateTime.now();
        dto.updatedAt = LocalDateTime.now();
        return dto;

    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public String getLanguage() { return language; }
    public String getAnswer() { return answer; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
