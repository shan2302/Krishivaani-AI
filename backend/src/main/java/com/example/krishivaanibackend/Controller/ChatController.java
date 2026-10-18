package com.example.krishivaanibackend.Controller;


import com.example.krishivaanibackend.dto.ApiResponse;
import com.example.krishivaanibackend.dto.ChatRequestDTO;
import com.example.krishivaanibackend.dto.ChatResponseDTO;
import com.example.krishivaanibackend.entity.Question;
import com.example.krishivaanibackend.exception.ResourceNotFoundException;
import com.example.krishivaanibackend.service.QuestionService;
import com.example.krishivaanibackend.service.RagService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final QuestionService questionService;
    private final RagService ragService;

    public ChatController(QuestionService questionService, RagService ragService) {
        this.questionService = questionService;
        this.ragService = ragService;
    }
    /**
     * POST /api/v1/chat/message
     * (Also maps to POST /api/v1/chat for RESTful convenience)
     *
     * Processes a farmer's question:
     * 1. Saves question to PostgreSQL.
     * 2. Executes RAG query against PgVector and Groq LLM.
     * 3. Updates record with generated answer.
     * 4. Returns 201 Created with structured ApiResponse.
     */
    @PostMapping({"","/message"})
    public ResponseEntity<ApiResponse<ChatResponseDTO>> sendMessage(
            @Valid @RequestBody ChatRequestDTO request
            ){
        log.info("Received chat message in language [{}]: {}", request.getLanguage(), request.getContent());

//        1. Save question in DB
        Question saved = questionService.submitQuestion(
                request.getContent(),
                request.getLanguage()
        );

//        2. Execute RAG pipeline (Retrieval -> Augmentation -> LLM generation)
        String aiAnswer = ragService.answerWithRag(
                request.getContent(),
                request.getLanguage()
        );

//        3. Persist the AI-generated answer
        Question updated = questionService.updateAnswer(
                saved.getId(),
                aiAnswer
        );

//        4. Return structured API response
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(
                        ChatResponseDTO.from(updated),
                        "Answer generated successfully"
                ));
    }


    /**
     * GET /api/v1/chat/history
     * Retrieves all past questions and answers for the farmer.
     */

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ChatResponseDTO>>> getChatHistory(){
        List<ChatResponseDTO> history = questionService.getAllQuestions()
                .stream()
                .map(ChatResponseDTO::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(history, "Chat history retrieved successfully"));
    }

    /**
     * GET /api/v1/chat/history/{id}
     * Retrieves a single chat message by ID
     */
    @GetMapping("/history/{id}")
    public ResponseEntity<ApiResponse<ChatResponseDTO>> getMessageById(@PathVariable Long id){
        Question q = questionService.getQuestionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat message",id));
        return ResponseEntity.ok(ApiResponse.ok(ChatResponseDTO.from(q), "Chat message retrieved successfully"));
    }

    /**
     * DELETE /api/v1/chat/history/{id}
     * Deletes a single chat message by ID
     */
    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id){
        if (!questionService.exists(id)){
            throw new ResourceNotFoundException("Chat message",id);
        }
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
