
package com.example.krishivaanibackend.Controller;

import com.example.krishivaanibackend.dto.ApiResponse;
import com.example.krishivaanibackend.dto.QuestionRequestDTO;
import com.example.krishivaanibackend.dto.QuestionResponseDTO;
import com.example.krishivaanibackend.entity.Question;
import com.example.krishivaanibackend.service.QuestionService;

import jakarta.validation.Valid;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final QuestionService questionService;
    private final OllamaChatModel chatModel;

    public AiController(
            QuestionService questionService,
            OllamaChatModel chatModel) {
        this.questionService = questionService;
        this.chatModel = chatModel;
    }

    // POST /api/v1/ai/ask
    // Accepts a question, generates an AI answer,
    // and saves both the question and answer.

    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<QuestionResponseDTO>> ask(
            @Valid @RequestBody QuestionRequestDTO request) {

        // 1. Save the question in the database
        Question saved = questionService.submitQuestion(
                request.getContent(),
                request.getLanguage()
        );

        // 2. Generate an answer using the local Ollama model
        String prompt = "Answer the following agricultural question "
                + "in the requested language: "
                + request.getLanguage()
                + "\n\nQuestion: "
                + request.getContent();

        String aiAnswer = chatModel.call(prompt);

        // 3. Save the generated answer in the database
        Question withAnswer = questionService.updateAnswer(
                saved.getId(),
                aiAnswer
        );

        // 4. Return the response
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.ok(
                                QuestionResponseDTO.from(withAnswer),
                                "Question answered successfully"
                        )
                );
    }
}
