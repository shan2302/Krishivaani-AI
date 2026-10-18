package com.example.krishivaanibackend.Controller;

import com.example.krishivaanibackend.config.JwtAuthenicationFilter;
import com.example.krishivaanibackend.dto.ChatRequestDTO;
import com.example.krishivaanibackend.entity.Question;
import com.example.krishivaanibackend.exception.GlobalExceptionHandler;
import com.example.krishivaanibackend.service.JwtService;
import com.example.krishivaanibackend.service.QuestionService;
import com.example.krishivaanibackend.service.RagService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private QuestionService questionService;

    @MockitoBean
    private RagService ragService;

    @MockitoBean
    private JwtAuthenicationFilter jwtAuthenicationFilter;

    @MockitoBean
    private JwtService jwtService;

    private Question sampleQuestion;

    @BeforeEach
    void setUp() {
        sampleQuestion = new Question("What is the seed rate for wheat?", "en");
        sampleQuestion.setAnswer("ICAR recommends 100-125 kg/ha for timely sown wheat.");
    }

    @Test
    @DisplayName("POST /api/v1/chat/message - Success returns 201 Created")
    void testSendMessage_Success() throws Exception {
        ChatRequestDTO request = new ChatRequestDTO("What is the seed rate for wheat?", "en");

        when(questionService.submitQuestion(anyString(), anyString())).thenReturn(sampleQuestion);
        when(ragService.answerWithRag(anyString(), anyString())).thenReturn("ICAR recommends 100-125 kg/ha for timely sown wheat.");
        when(questionService.updateAnswer(any(), anyString())).thenReturn(sampleQuestion);

        mockMvc.perform(post("/api/v1/chat/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.answer").value("ICAR recommends 100-125 kg/ha for timely sown wheat."))
                .andExpect(jsonPath("$.message").value("Answer generated successfully"));

        verify(questionService, times(1)).submitQuestion("What is the seed rate for wheat?", "en");
        verify(ragService, times(1)).answerWithRag("What is the seed rate for wheat?", "en");
        verify(questionService, times(1)).updateAnswer(any(), eq("ICAR recommends 100-125 kg/ha for timely sown wheat."));
    }

    @Test
    @DisplayName("POST /api/v1/chat/message - Blank content returns 400 Bad Request")
    void testSendMessage_ValidationFailure_BlankContent() throws Exception {
        ChatRequestDTO request = new ChatRequestDTO("", "en");

        mockMvc.perform(post("/api/v1/chat/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));

        verifyNoInteractions(questionService, ragService);
    }

    @Test
    @DisplayName("POST /api/v1/chat/message - Content less than 3 chars returns 400 Bad Request")
    void testSendMessage_ValidationFailure_TooShortContent() throws Exception {
        ChatRequestDTO request = new ChatRequestDTO("Hi", "en");

        mockMvc.perform(post("/api/v1/chat/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));

        verifyNoInteractions(questionService, ragService);
    }

    @Test
    @DisplayName("GET /api/v1/chat/history - Returns list of chat messages")
    void testGetChatHistory_Success() throws Exception {
        when(questionService.getAllQuestions()).thenReturn(List.of(sampleQuestion));

        mockMvc.perform(get("/api/v1/chat/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].content").value("What is the seed rate for wheat?"))
                .andExpect(jsonPath("$.data[0].answer").value("ICAR recommends 100-125 kg/ha for timely sown wheat."));

        verify(questionService, times(1)).getAllQuestions();
    }

    @Test
    @DisplayName("GET /api/v1/chat/history/{id} - Existing ID returns 200 OK")
    void testGetMessageById_Found() throws Exception {
        when(questionService.getQuestionById(1L)).thenReturn(Optional.of(sampleQuestion));

        mockMvc.perform(get("/api/v1/chat/history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value("What is the seed rate for wheat?"));

        verify(questionService, times(1)).getQuestionById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/chat/history/{id} - Non-existent ID returns 404 Not Found")
    void testGetMessageById_NotFound() throws Exception {
        when(questionService.getQuestionById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/chat/history/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(questionService, times(1)).getQuestionById(999L);
    }

    @Test
    @DisplayName("DELETE /api/v1/chat/history/{id} - Deletes message returns 204 No Content")
    void testDeleteMessage_Success() throws Exception {
        when(questionService.exists(1L)).thenReturn(true);
        doNothing().when(questionService).deleteQuestion(1L);

        mockMvc.perform(delete("/api/v1/chat/history/1"))
                .andExpect(status().isNoContent());

        verify(questionService, times(1)).exists(1L);
        verify(questionService, times(1)).deleteQuestion(1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/chat/history/{id} - Non-existent ID returns 404 Not Found")
    void testDeleteMessage_NotFound() throws Exception {
        when(questionService.exists(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/chat/history/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(questionService, times(1)).exists(999L);
        verify(questionService, never()).deleteQuestion(anyLong());
    }
}
