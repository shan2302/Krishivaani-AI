package com.example.krishivaanibackend.repository;

import com.example.krishivaanibackend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

//    Find all questions submitted in a specific Language(e.g. "en", "hi", "ka")
    List<Question> findByLanguage(String language);

//    Find all questions that have not yet received an AI answer
    List<Question> findByAnswerIsNull();

//    Find all questions that already have an AI answer
    List<Question> findByAnswerIsNotNull();
}
