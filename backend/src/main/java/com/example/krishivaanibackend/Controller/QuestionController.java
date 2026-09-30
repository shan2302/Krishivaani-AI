package com.example.krishivaanibackend.Controller;

import com.example.krishivaanibackend.entity.Question;
import com.example.krishivaanibackend.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Question> submitQuestion(@RequestBody Map<String, String> body){
        String content = body.get("content");
        String language = body.getOrDefault("language","en");
        if(content == null || content.isBlank()){
            return ResponseEntity.badRequest().build();
        }
        Question saved = questionService.submitQuestion(content,language);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(saved);
    }
//    GET /api/v1/questions - Get all questions
    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions(){
        return ResponseEntity.ok(questionService.getAllQuestions());
    }
//    GET /api/v1/questions/{id} - Get a specific question by ID
    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestion(@PathVariable Long id){
        return questionService.getQuestionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
//    DELETE /api/v1/questions/{id} - Delete a specific question by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id){
        if(!questionService.exists(id)){
            return ResponseEntity.notFound().build();
        }
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
