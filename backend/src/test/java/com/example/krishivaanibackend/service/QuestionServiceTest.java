package com.example.krishivaanibackend.service;

import com.example.krishivaanibackend.entity.Question;
import com.example.krishivaanibackend.exception.ResourceNotFoundException;
import com.example.krishivaanibackend.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.StatusResultMatchersExtensionsKt.isEqualTo;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionService questionService;

    private Question sampleQuestion;

    @BeforeEach
    void setUp(){
        sampleQuestion = new Question("What fertilizer is best for wheat?", "en");
    }

    @Test
    void submitQuestion_savesAndReturnsQuestion(){
        when(questionRepository.save(any(Question.class))).thenReturn(sampleQuestion);
        Question result = questionService.submitQuestion("What fertilizer is best for wheat?","en");
        assertThat(result.getContent()).isEqualTo("What fertilizer is best for wheat?");
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    void getAllQuestions_returnListFromRepo(){
        when(questionRepository.findAll()).thenReturn(List.of(sampleQuestion));

        List<Question> result = questionService.getAllQuestions();
        assertThat(result).hasSize(1);
        verify(questionRepository).findAll();
    }

    @Test
    void getQuestionById_returnsQuestions_whenExists(){
        when(questionRepository.findById(1L)).thenReturn(Optional.of(sampleQuestion));

        Optional<Question> result = questionService.getQuestionById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getLanguage()).isEqualTo("en");
    }

    @Test
    void getQuestionById_returnsEmpty_whenNotFound(){
        when(questionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Question> result = questionService.getQuestionById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void deleteQuestion_callRepositoryDelete(){
        doNothing().when(questionRepository).deleteById(1L);

        questionService.deleteQuestion(1L);

        verify(questionRepository).deleteById(1L);
    }

    @Test
    void updateAnswer_updatesAndReturns_whenFound(){
        when(questionRepository.findById(1L)).thenReturn(Optional.of(sampleQuestion));
        when(questionRepository.save(any(Question.class))).thenReturn(sampleQuestion);

        Question result = questionService.updateAnswer(1L, "Use area 46%");

        assertThat(result.getAnswer()).isEqualTo("Use area 46%");
    }

    @Test
    void updateAnswer_throws_whenNotFound(){
        when(questionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.updateAnswer(99L, "Some answer"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

}
