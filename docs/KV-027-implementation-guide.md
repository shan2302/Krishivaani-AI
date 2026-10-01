# KV-027 — Implement Core REST API Structure
**Assignee:** Shantanu | **Priority:** High | **Status:** TO DO | **Points:** 5

> Audited against KV-025 and KV-026 output. Written on 2026-10-02.

---

## Context — What's Already Built

From KV-025 + KV-026, you already have:

| What exists | Endpoint |
|-------------|----------|
| `HealthController` | `GET /api/v1/health` → plain string |
| `QuestionController` | `POST /api/v1/questions` → create |
| `QuestionController` | `GET /api/v1/questions` → list all |
| `QuestionController` | `GET /api/v1/questions/{id}` → get one |
| `QuestionController` | `DELETE /api/v1/questions/{id}` → delete |
| `VoiceController` | `POST /api/v1/voice/transcribe` → stub |

---

## What KV-027 Adds

KV-027 is about **completing the REST API surface** — making the existing endpoints production-ready and adding the missing AI query endpoint.

| # | Task | New/Changed |
|---|------|-------------|
| 1 | Add `ApiResponse<T>` wrapper DTO for consistent success envelope | New |
| 2 | Upgrade `HealthController` to return JSON (not a raw string) | Change |
| 3 | Add `PATCH /api/v1/questions/{id}/answer` — store AI answer on a question | New |
| 4 | Add `POST /api/v1/ai/ask` — the main AI query endpoint (stub for now) | New |
| 5 | Run app and test every endpoint end-to-end | Verify |

---

## Step 1 — Create `ApiResponse<T>` Wrapper DTO

**Path:** `dto/ApiResponse.java`

Wraps every success response so the frontend always gets:
```json
{ "success": true, "data": { ... }, "message": "OK" }
```

```java
package com.example.krishivaanibackend.dto;

/**
 * Generic success response envelope for all API endpoints.
 */
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;

    private ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data    = data;
        this.message = message;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, "OK");
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }

    // Getters
    public boolean isSuccess() { return success; }
    public T getData()         { return data; }
    public String getMessage() { return message; }
}
```

---

## Step 2 — Upgrade `HealthController` to return JSON

Replace the entire `HealthController.java`:

```java
package com.example.krishivaanibackend.Controller;

import com.example.krishivaanibackend.dto.ApiResponse;
import com.example.krishivaanibackend.service.HealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    // GET /api/v1/health
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> getHealth() {
        Map<String, String> data = Map.of(
                "status",  "UP",
                "message", healthService.getStatus()
        );
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}
```

---

## Step 3 — Add PATCH answer endpoint

### 3a. Create `AnswerRequestDTO.java`

**Path:** `dto/AnswerRequestDTO.java`

```java
package com.example.krishivaanibackend.dto;

import jakarta.validation.constraints.NotBlank;

/** Request body for PATCH /api/v1/questions/{id}/answer */
public class AnswerRequestDTO {

    @NotBlank(message = "Answer must not be blank")
    private String answer;

    public AnswerRequestDTO() {}

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}
```

### 3b. Add `updateAnswer()` + class-level `@Transactional` to `QuestionService`

Add `@Transactional` at class level (above `@Service`) and add this method before the closing `}`:

```java
    public Question updateAnswer(Long id, String answer) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new com.example.krishivaanibackend
                        .exception.ResourceNotFoundException("Question", id));
        question.setAnswer(answer);
        return questionRepository.save(question);
    }
```

### 3c. Add PATCH method to `QuestionController`

Add these two imports:
```java
import com.example.krishivaanibackend.dto.AnswerRequestDTO;
import com.example.krishivaanibackend.dto.ApiResponse;
```

Add this method before the closing `}`:
```java
    // PATCH /api/v1/questions/{id}/answer
    @PatchMapping("/{id}/answer")
    public ResponseEntity<ApiResponse<QuestionResponseDTO>> updateAnswer(
            @PathVariable Long id,
            @Valid @RequestBody AnswerRequestDTO request) {

        Question updated = questionService.updateAnswer(id, request.getAnswer());
        return ResponseEntity.ok(ApiResponse.ok(QuestionResponseDTO.from(updated), "Answer saved"));
    }
```

---

## Step 4 — Create `AiController`

**Path:** `Controller/AiController.java`

```java
package com.example.krishivaanibackend.Controller;

import com.example.krishivaanibackend.dto.ApiResponse;
import com.example.krishivaanibackend.dto.QuestionRequestDTO;
import com.example.krishivaanibackend.dto.QuestionResponseDTO;
import com.example.krishivaanibackend.entity.Question;
import com.example.krishivaanibackend.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Core AI query endpoint.
 * POST /api/v1/ai/ask — saves the question and returns a stub answer.
 * Real RAG + LLM wiring is done in the AI integration sprint.
 */
@RestController
@RequestMapping("/api/v1/ai")
@CrossOrigin(origins = "*")
public class AiController {

    private final QuestionService questionService;

    public AiController(QuestionService questionService) {
        this.questionService = questionService;
    }

    // POST /api/v1/ai/ask
    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<QuestionResponseDTO>> ask(
            @Valid @RequestBody QuestionRequestDTO request) {

        // 1. Save question
        Question saved = questionService.submitQuestion(
                request.getContent(), request.getLanguage());

        // 2. TODO: RAG pipeline + LLM call (AI integration sprint)
        String stubAnswer = "[AI answer will appear here after AI integration sprint]";

        // 3. Store stub answer
        Question withAnswer = questionService.updateAnswer(saved.getId(), stubAnswer);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(QuestionResponseDTO.from(withAnswer), "Question received"));
    }
}
```

---

## Step 5 — Run & Verify

```bash
# Start
cd backend
./mvnw spring-boot:run

# Health → must return JSON now
curl http://localhost:8080/api/v1/health

# Validation → must return 400 with details array
curl -X POST http://localhost:8080/api/v1/questions \
  -H "Content-Type: application/json" -d '{}'

# Create question
curl -X POST http://localhost:8080/api/v1/questions \
  -H "Content-Type: application/json" \
  -d '{"content": "Which fertilizer for wheat?", "language": "en"}'

# PATCH answer (use id from above)
curl -X PATCH http://localhost:8080/api/v1/questions/1/answer \
  -H "Content-Type: application/json" \
  -d '{"answer": "Use DAP for wheat."}'

# AI ask endpoint
curl -X POST http://localhost:8080/api/v1/ai/ask \
  -H "Content-Type: application/json" \
  -d '{"content": "Best crop rotation for clay soil?", "language": "en"}'

# 404 test
curl http://localhost:8080/api/v1/questions/9999

# Delete
curl -X DELETE http://localhost:8080/api/v1/questions/1
```

---

## New Files After KV-027

```
dto/
  ApiResponse.java            ← NEW
  AnswerRequestDTO.java        ← NEW

Controller/
  AiController.java           ← NEW
  HealthController.java        ← updated (returns JSON)
  QuestionController.java      ← updated (PATCH answer added)

service/
  QuestionService.java         ← updated (updateAnswer + @Transactional on class)
```

---

## Completion Checklist

- [ ] `dto/ApiResponse.java` created
- [ ] `dto/AnswerRequestDTO.java` created
- [ ] `HealthController` returns `ApiResponse<Map>` JSON
- [ ] `QuestionService` has `updateAnswer()` + class-level `@Transactional`
- [ ] `QuestionController` has `PATCH /{id}/answer`
- [ ] `AiController.java` created with `POST /api/v1/ai/ask`
- [ ] `./mvnw compile` → BUILD SUCCESS
- [ ] App starts without errors
- [ ] `GET /api/v1/health` → JSON (not plain string)
- [ ] `POST /api/v1/questions` empty body → 400 with `details` array
- [ ] `GET /api/v1/questions/9999` → 404 JSON body
- [ ] `PATCH /api/v1/questions/1/answer` → 200 with `ApiResponse` envelope
- [ ] `POST /api/v1/ai/ask` → 201 with stub answer in `data`
