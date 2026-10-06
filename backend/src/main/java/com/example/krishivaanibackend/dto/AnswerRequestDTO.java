package com.example.krishivaanibackend.dto;

import jakarta.validation.constraints.NotBlank;

public class AnswerRequestDTO {
    @NotBlank(message = "Answer cannot be blank")
    private String answer;
    public AnswerRequestDTO() {}

    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
