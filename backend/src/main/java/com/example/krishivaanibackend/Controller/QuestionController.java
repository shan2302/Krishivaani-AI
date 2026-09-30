package com.example.krishivaanibackend.Controller;

import com.example.krishivaanibackend.service.QuestionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {
    private final QuestionService questionService;
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }
}
