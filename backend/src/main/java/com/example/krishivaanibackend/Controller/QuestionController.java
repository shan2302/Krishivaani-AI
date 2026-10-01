package com.example.krishivaanibackend.Controller;

import com.example.krishivaanibackend.dto.QuestionRequestDTO;
import com.example.krishivaanibackend.dto.QuestionResponseDTO;
import com.example.krishivaanibackend.entity.Question;
import com.example.krishivaanibackend.exception.ResourceNotFoundException;
import com.example.krishivaanibackend.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
@CrossOrigin(origins = "*")
public class QuestionController {
    private final QuestionService questionService;
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

//    POST /api/v1/questions - Submit a new question
    @PostMapping
    public ResponseEntity<QuestionResponseDTO> submitQuestion(@Valid @RequestBody QuestionRequestDTO request)
    {
        Question saved = questionService.submitQuestion(request.getContent(), request.getLanguage());
        return ResponseEntity.status(HttpStatus.CREATED).body(QuestionResponseDTO.from(saved));
    }

//    GET /api/v1/questions - Get all questions
    @GetMapping
    public ResponseEntity<List<QuestionResponseDTO>> getAllQuestions(){
        List<QuestionResponseDTO> list = questionService.getAllQuestions()
                .stream()
                .map(QuestionResponseDTO::from)
                .toList();
        return ResponseEntity.ok(list);
    }

//    GET /api/v1/questions/{id} - Get a specific question by ID
    @GetMapping("/{id}")
   public ResponseEntity<QuestionResponseDTO> getQuestion(@PathVariable Long id){
        Question q = questionService.getQuestionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question",id));
        return ResponseEntity.ok(QuestionResponseDTO.from(q));
    }

//    DELETE /api/v1/questions/{id} - Delete a specific question by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        if (!questionService.exists(id)) {
            throw new ResourceNotFoundException("Question", id);
        }
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
