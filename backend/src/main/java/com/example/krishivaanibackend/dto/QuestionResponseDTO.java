package com.example.krishivaanibackend.dto;

import com.example.krishivaanibackend.entity.Question;

import java.time.LocalDateTime;

//Outgoing response for question endpoints - never exposes raw entity
public class QuestionResponseDTO {
    private Long id;
    private String content;
    private String language;
    private String answer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

//    Static Factory :- Converts entity - DTO
    public static QuestionResponseDTO from(Question question)
    {
        QuestionResponseDTO dto = new QuestionResponseDTO();
        dto.id = question.getId();
        dto.content = question.getContent();
        dto.language = question.getLanguage();
        dto.answer = question.getAnswer();
        dto.createdAt = question.getCreatedAt();
        dto.updatedAt = question.getUpdatedAt();
        return dto;
    }
//    Getters
    public Long getId() { return id; }
    public String getContent() { return content; }
    public String getLanguage() { return language; }
    public String getAnswer() { return answer; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

}
