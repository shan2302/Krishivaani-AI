
package com.example.krishivaanibackend.Controller;

import com.example.krishivaanibackend.dto.ApiResponse;
import com.example.krishivaanibackend.dto.QuestionRequestDTO;
import com.example.krishivaanibackend.dto.QuestionResponseDTO;
import com.example.krishivaanibackend.entity.Question;
import com.example.krishivaanibackend.service.AIService;
import com.example.krishivaanibackend.service.EmbeddingService;
import com.example.krishivaanibackend.service.QuestionService;

import com.example.krishivaanibackend.service.RagService;
import jakarta.validation.Valid;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final QuestionService questionService;
    private final AIService aiService;
    private final RagService ragService;
    private final EmbeddingService embeddingService;

    public AiController(
            QuestionService questionService,
            AIService aiService, RagService ragService, EmbeddingService embeddingService) {
        this.questionService = questionService;
        this.aiService = aiService;
        this.ragService = ragService;
        this.embeddingService = embeddingService;
    }

    // POST /api/v1/ai/ask
    // Accepts a question, generates an AI answer,
    // and saves both the question and answer.

    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<QuestionResponseDTO>> ask(
            @Valid @RequestBody QuestionRequestDTO request
    )
    {
//       1. Save qeustion in DB
        Question saved = questionService.submitQuestion(
                request.getContent(),
                request.getLanguage()
        );

//        2. Generate Answer via Grq LLM
        String aiAnswer = ragService.answerWithRag(
                request.getContent(),
                request.getLanguage()
        );

//        3. Update the answer in DB
        Question withAnswer = questionService.updateAnswer(
                saved.getId(),
                aiAnswer
        );

//        4. Return API response
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(
                        QuestionResponseDTO.from(withAnswer),
                        "Question Answered successfully"
                ));
    }
//    Add new admin endpoint:
    @PostMapping("/ingest")
    public ResponseEntity<ApiResponse<String>> ingest() {
        int count = embeddingService.embedAll();
        return ResponseEntity.ok(ApiResponse.ok("Ingestion complete", "Successfully embedded " + count + " documents into vector store"));
    }
}