# KV-077: Implement ChatController REST Endpoint

| Metadata | Details |
| :--- | :--- |
| **Jira Issue** | **KV-077** |
| **Task Name** | Implement ChatController REST endpoint |
| **Epic** | Backend & REST APIs (Week 4) |
| **Story Points** | 5 |
| **Priority** | High |
| **Assignee** | Shantanu |
| **Status** | In Progress (TO DO &rarr; IN PROGRESS &rarr; DONE) |
| **Labels** | `backend`, `rest`, `spring-boot`, `api` |

---

## 1. Executive Summary & Objective

The objective of **KV-077** is to design, implement, and thoroughly test the dedicated `ChatController` REST controller in the Spring Boot backend (`krishivaanibackend`). 

Prior to this task, the prototype question-answering functionality lived inside `AiController` (`/api/v1/ai/ask`) alongside administrative ingestion endpoints. In this task, we decouple the farmer-facing conversational chat API into a dedicated, clean, RESTful controller:
- Base path: `/api/v1/chat`
- Supports sending conversational queries in English, Hindi, and Kannada.
- Integrates with the Retrieval-Augmented Generation (RAG) engine (`RagService`), leveraging ICAR verified documents stored in PgVector and the Groq LLM (`openai/gpt-oss-120b`).
- Persists all conversation messages and answers into PostgreSQL via `QuestionService` for auditability and history tracking.
- Provides chat history retrieval (`GET /api/v1/chat/history`) and management endpoints.
- Configures Spring Security to permit public farmer access while respecting CORS policies.
- Connects directly with the React frontend chat interface (`frontend/src/components/Chat.jsx`).

---

## 2. Architecture & Request-Response Flow

```
                      +-----------------------------+
                      |   React Web / Mobile Client  |
                      |   (Vite + React Chat UI)    |
                      +--------------+--------------+
                                     |
                                     | POST /api/v1/chat/message
                                     | Body: { "content": "...", "language": "hi" }
                                     v
                      +-----------------------------+
                      |      SecurityConfig         |
                      |  (PermitAll /api/v1/chat/**)|
                      +--------------+--------------+
                                     |
                                     v
                      +-----------------------------+
                      |       ChatController        |
                      |      (@RestController)      |
                      +-------+-------------+-------+
                              |             |
           1. Save Question   |             | 2. Generate RAG Answer
                              v             v
             +------------------+    +------------------+
             | QuestionService  |    |    RagService    |
             +--------+---------+    +--------+---------+
                      |                       |
                      |                       | 2a. Query VectorStore (PgVector)
                      |                       | 2b. Augment Prompt with ICAR Context
                      |                       | 2c. Call LLM (Groq via Spring AI)
                      v                       v
             +------------------+    +------------------+
             | PostgreSQL DB    |    | External LLM     |
             | (questions table)|    | (Groq / Gemini)  |
             +------------------+    +------------------+
                              |
                              | 3. Update Answer in DB
                              v
             +----------------------------------+
             | Return: ApiResponse<ChatResponseDTO>
             | Status: 201 CREATED              |
             +----------------------------------+
```

---

## 3. Detailed Step-by-Step Implementation Guide

Follow each step below in order.

### Step 1: Create or Verify the DTOs

We need standard Request and Response DTOs. We can create dedicated `ChatRequestDTO` and `ChatResponseDTO`, or standardize upon `QuestionRequestDTO` and `QuestionResponseDTO`.

Creating dedicated `ChatRequestDTO` and `ChatResponseDTO` ensures that the chat API can evolve independently (e.g., adding `sessionId`, `model`, `confidenceScore` in the future) while maintaining full backward compatibility.

#### 1.1 Create `ChatRequestDTO.java`
File path: `backend/src/main/java/com/example/krishivaanibackend/dto/ChatRequestDTO.java`

```java
package com.example.krishivaanibackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChatRequestDTO {

    @NotBlank(message = "Message content cannot be blank")
    @Size(min = 3, max = 2000, message = "Message content must be between 3 and 2000 characters")
    private String content;

    @Size(max = 10, message = "Language code must not exceed 10 characters")
    private String language = "en";

    public ChatRequestDTO() {}

    public ChatRequestDTO(String content, String language) {
        this.content = content;
        this.language = (language != null && !language.isBlank()) ? language : "en";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
```

#### 1.2 Create `ChatResponseDTO.java`
File path: `backend/src/main/java/com/example/krishivaanibackend/dto/ChatResponseDTO.java`

```java
package com.example.krishivaanibackend.dto;

import com.example.krishivaanibackend.entity.Question;
import java.time.LocalDateTime;

public class ChatResponseDTO {

    private Long id;
    private String content;
    private String language;
    private String answer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ChatResponseDTO() {}

    public static ChatResponseDTO from(Question question) {
        ChatResponseDTO dto = new ChatResponseDTO();
        dto.id = question.getId();
        dto.content = question.getContent();
        dto.language = question.getLanguage();
        dto.answer = question.getAnswer();
        dto.createdAt = question.getCreatedAt();
        dto.updatedAt = question.getUpdatedAt();
        return dto;
    }

    public static ChatResponseDTO of(Long id, String content, String language, String answer) {
        ChatResponseDTO dto = new ChatResponseDTO();
        dto.id = id;
        dto.content = content;
        dto.language = language;
        dto.answer = answer;
        dto.createdAt = LocalDateTime.now();
        dto.updatedAt = LocalDateTime.now();
        return dto;
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public String getLanguage() { return language; }
    public String getAnswer() { return answer; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
```

---

### Step 2: Implement `ChatController.java`

Create the controller under `backend/src/main/java/com/example/krishivaanibackend/Controller/ChatController.java`.

#### Responsibilities:
1. `POST /api/v1/chat/message` (or `POST /api/v1/chat`): Accepts user question, executes RAG retrieval + LLM synthesis, persists both question and answer, returns HTTP 201 Created.
2. `GET /api/v1/chat/history`: Retrieves the list of past messages.
3. `GET /api/v1/chat/history/{id}`: Retrieves a specific chat message by ID (or throws 404).
4. `DELETE /api/v1/chat/history/{id}`: Deletes a message by ID.

```java
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
    @PostMapping({"", "/message"})
    public ResponseEntity<ApiResponse<ChatResponseDTO>> sendMessage(
            @Valid @RequestBody ChatRequestDTO request
    ) {
        log.info("Received chat message in language [{}]: {}", request.getLanguage(), request.getContent());

        // 1. Save question to database
        Question saved = questionService.submitQuestion(
                request.getContent(),
                request.getLanguage()
        );

        // 2. Execute RAG pipeline (Retrieval -> Augmentation -> LLM Generation)
        String aiAnswer = ragService.answerWithRag(
                request.getContent(),
                request.getLanguage()
        );

        // 3. Persist the AI-generated answer
        Question updated = questionService.updateAnswer(
                saved.getId(),
                aiAnswer
        );

        // 4. Return standard ApiResponse
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(
                        ChatResponseDTO.from(updated),
                        "Answer generated successfully"
                ));
    }

    /**
     * GET /api/v1/chat/history
     * Retrieves all past questions and answers.
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ChatResponseDTO>>> getChatHistory() {
        List<ChatResponseDTO> history = questionService.getAllQuestions()
                .stream()
                .map(ChatResponseDTO::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.ok(history, "Chat history retrieved successfully"));
    }

    /**
     * GET /api/v1/chat/history/{id}
     * Retrieves a single chat message by ID.
     */
    @GetMapping("/history/{id}")
    public ResponseEntity<ApiResponse<ChatResponseDTO>> getMessageById(@PathVariable Long id) {
        Question q = questionService.getQuestionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChatMessage", id));

        return ResponseEntity.ok(ApiResponse.ok(ChatResponseDTO.from(q)));
    }

    /**
     * DELETE /api/v1/chat/history/{id}
     * Deletes a chat message by ID.
     */
    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        if (!questionService.exists(id)) {
            throw new ResourceNotFoundException("ChatMessage", id);
        }
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

### Step 3: Configure Spring Security

Update `backend/src/main/java/com/example/krishivaanibackend/config/SecurityConfig.java`.

Ensure that the `/api/v1/chat/**` endpoints are publicly accessible so farmers do not need prior authentication to converse with KrishiVaani AI:

```java
.authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/api/v1/health/**").permitAll()
        .requestMatchers("/api/v1/ai/**").permitAll()
        .requestMatchers("/api/v1/chat/**").permitAll()   // <--- Add this line
        .anyRequest().authenticated()
)
```

---

### Step 4: Synchronize Frontend Configuration

Update `frontend/.env` to point to the new dedicated chat endpoint:

```properties
VITE_CHAT_API_URL=http://localhost:8080/api/v1/chat/message
```

In `frontend/src/components/Chat.jsx`, the response parser already extracts the answer using:
```javascript
const answer =
  data.data?.answer ??
  data.data?.content ??
  data.data?.response ??
  data.message ??
  data.data?.message;
```
Because `ApiResponse.ok(ChatResponseDTO.from(updated))` returns:
```json
{
  "success": true,
  "data": {
    "id": 15,
    "content": "What is the recommended seed rate for wheat?",
    "language": "en",
    "answer": "According to ICAR guidelines, the recommended seed rate is 100-125 kg/ha...",
    "createdAt": "2026-10-19T02:00:00",
    "updatedAt": "2026-10-19T02:00:02"
  },
  "message": "Answer generated successfully"
}
```
The frontend immediately displays the answer with zero breaking changes!

---

### Step 5: Implement Comprehensive Unit & Integration Tests

Create `backend/src/test/java/com/example/krishivaanibackend/Controller/ChatControllerTest.java` to verify:
1. `POST /api/v1/chat/message` with valid payload returns HTTP 201 Created and correct answer.
2. `POST /api/v1/chat/message` with blank content returns HTTP 400 Bad Request.
3. `POST /api/v1/chat/message` with content shorter than 3 characters returns HTTP 400 Bad Request.
4. `GET /api/v1/chat/history` returns HTTP 200 OK with list.
5. `GET /api/v1/chat/history/{id}` returns HTTP 200 OK when found, and 404 Not Found when absent.
6. `DELETE /api/v1/chat/history/{id}` returns HTTP 204 No Content.

#### Test Class Implementation:
File path: `backend/src/test/java/com/example/krishivaanibackend/Controller/ChatControllerTest.java`

```java
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
    }

    @Test
    @DisplayName("GET /api/v1/chat/history/{id} - Non-existent ID returns 404 Not Found")
    void testGetMessageById_NotFound() throws Exception {
        when(questionService.getQuestionById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/chat/history/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("DELETE /api/v1/chat/history/{id} - Deletes message returns 204 No Content")
    void testDeleteMessage_Success() throws Exception {
        when(questionService.exists(1L)).thenReturn(true);
        doNothing().when(questionService).deleteQuestion(1L);

        mockMvc.perform(delete("/api/v1/chat/history/1"))
                .andExpect(status().isNoContent());

        verify(questionService, times(1)).deleteQuestion(1L);
    }
}
```

---

### Step 6: Compilation & Automated Verification

Run Maven commands in the terminal from `backend/`:

```bash
# 1. Compile the backend project
./mvnw clean compile

# 2. Run the newly created ChatController test suite
./mvnw test -Dtest=ChatControllerTest

# 3. Run the full test suite to ensure zero regressions
./mvnw test
```

---

### Step 7: Manual Testing via cURL / Postman

#### 7.1 Ask a Question in English (cURL)
```bash
curl -X POST http://localhost:8080/api/v1/chat/message \
  -H "Content-Type: application/json" \
  -d '{
    "content": "What is the recommended spacing for tomato planting?",
    "language": "en"
  }'
```

#### 7.2 Ask a Question in Hindi (cURL)
```bash
curl -X POST http://localhost:8080/api/v1/chat/message \
  -H "Content-Type: application/json" \
  -d '{
    "content": "गेहूं की बुवाई के लिए प्रति हेक्टेयर कितना बीज चाहिए?",
    "language": "hi"
  }'
```

#### 7.3 Fetch Chat History (cURL)
```bash
curl -X GET http://localhost:8080/api/v1/chat/history
```

---

## 4. Definition of Done (DoD) Checklist

Before resolving Jira task **KV-077**:
- [ ] `ChatRequestDTO.java` and `ChatResponseDTO.java` created with Jakarta validation constraints.
- [ ] `ChatController.java` implemented with `POST /message`, `GET /history`, `GET /history/{id}`, and `DELETE /history/{id}`.
- [ ] `SecurityConfig.java` updated with `.requestMatchers("/api/v1/chat/**").permitAll()`.
- [ ] `frontend/.env` configured with `VITE_CHAT_API_URL=http://localhost:8080/api/v1/chat/message`.
- [ ] `ChatControllerTest.java` written and passing all unit/mockMvc tests.
- [ ] Entire backend test suite passes (`./mvnw test`).
- [ ] Postman / cURL verification completed with live database and LLM.
- [ ] Jira Task KV-077 marked as **DONE**.
