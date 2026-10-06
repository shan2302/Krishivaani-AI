package com.example.krishivaanibackend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class QuestionRequestDTO {
    @NotBlank(message = "Question content cannot be blank")
    @Size(min=5,max=2000,message = "Contennt must be between 5 and 2000 characters")
    private String content;

    @Size(max=10,message = "Language code must be not exceed 10 characters")
    private String language = "en";

    public QuestionRequestDTO() {}
    public QuestionRequestDTO(String content, String language) {
        this.content = content;
        this.language = language;
    }

//    Getter and Setter
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
