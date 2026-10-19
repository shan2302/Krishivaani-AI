package com.example.krishivaanibackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChatRequestDTO {

    @NotBlank(message = "Message content cannot be blank")
    @Size(min=3, max=2000, message = "Message content must be between 3 and 2000 characters")

    private String content;

    @Size(max=10,message = "Language code must not exceed 10 characters")
    private String language = "en"; // default to English

    public ChatRequestDTO() {}

    public ChatRequestDTO(String content, String language) {
        this.content = content;
        this.language = language;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
