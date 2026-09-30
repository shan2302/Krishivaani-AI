package com.example.krishivaanibackend.service;


import com.example.krishivaanibackend.entity.Question;
import com.example.krishivaanibackend.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question submitQuestion(String content, String language) {
        Question question = new Question(content.trim(), language);
        return questionRepository.save(question);
    }

    @Transactional(readOnly = true)
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Question> getQuestionById(Long id){
        return questionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean exists(Long id){
        return questionRepository.existsById(id);
    }

    public void deleteQuestion(Long id){
        questionRepository.deleteById(id);
    }
    
}
