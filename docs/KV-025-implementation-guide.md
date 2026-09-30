# KV-025 — Layered Spring Boot Architecture
**Assignee:** Shantanu | **Status:** IN PROGRESS | **Points:** 5

---

## Checklist

| # | Task | File | Done? |
|---|------|------|-------|
| 1 | Add methods to `QuestionService` | `service/QuestionService.java` | ☐ |
| 2 | Add endpoints to `QuestionController` | `Controller/QuestionController.java` | ☐ |
| 3 | Add `@Service` to `SpeechService` | `service/SpeechService.java` | ☐ |
| 4 | Add `@Service` to `EmbeddingService` | `service/EmbeddingService.java` | ☐ |
| 5 | Wire `@RestController` to `VoiceController` | `Controller/VoiceController.java` | ☐ |
| 6 | Run app & test `/api/v1/health` → OK | — | ☐ |
| 7 | Test question endpoints via Postman/curl | — | ☐ |

---

## Already Done ✅

- `Question.java` entity
- `AgriculturalDocument.java` entity
- `QuestionRepository.java`
- `AgriculturalDocumentRepository.java`
- `HealthController.java` + `HealthService.java`
- `DatabaseConfig.java`
- `application.properties` (env vars only, safe to commit)

---

## Step 1 — QuestionService.java

Replace the empty class body with this:

```java
package com.example.krishivaanibackend.service;

import com.example.krishivaanibackend.entity.Question;
import com.example.krishivaanibackend.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
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
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean exists(Long id) {
        return questionRepository.existsById(id);
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
}
```

---

## Step 2 — QuestionController.java

Replace the empty class body with this:

```java
package com.example.krishivaanibackend.Controller;

import com.example.krishivaanibackend.entity.Question;
import com.example.krishivaanibackend.service.QuestionService;
import org.springframework.http.HttpStatus;
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

    // POST /api/v1/questions
    // Body: { "content": "...", "language": "hi" }
    @PostMapping
    public ResponseEntity<Question> submitQuestion(@RequestBody Map<String, String> body) {
        String content  = body.get("content");
        String language = body.getOrDefault("language", "en");
        if (content == null || content.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Question saved = questionService.submitQuestion(content, language);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // GET /api/v1/questions
    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    // GET /api/v1/questions/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestion(@PathVariable Long id) {
        return questionService.getQuestionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/v1/questions/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        if (!questionService.exists(id)) {
            return ResponseEntity.notFound().build();
        }
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## Step 3 — SpeechService.java

Add `@Service` and a stub method (real ASR comes in the voice pipeline task):

```java
package com.example.krishivaanibackend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SpeechService {

    public String transcribe(MultipartFile audioFile, String language) {
        // TODO: wire Whisper / Bhashini API here
        return "[ASR not integrated yet]";
    }
}
```

---

## Step 4 — EmbeddingService.java

Add `@Service` and wire the repository:

```java
package com.example.krishivaanibackend.service;

import com.example.krishivaanibackend.entity.AgriculturalDocument;
import com.example.krishivaanibackend.repository.AgriculturalDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmbeddingService {

    private final AgriculturalDocumentRepository documentRepository;

    public EmbeddingService(AgriculturalDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public AgriculturalDocument saveDocument(String title, String content,
                                              String category, String source) {
        return documentRepository.save(new AgriculturalDocument(title, content, category, source));
    }

    @Transactional(readOnly = true)
    public List<AgriculturalDocument> findByCategory(String category) {
        return documentRepository.findByCategory(category);
    }

    // TODO: add vector embedding methods when pgvector is set up
}
```

---

## Step 5 — VoiceController.java

Add `@RestController` and wire `SpeechService`:

```java
package com.example.krishivaanibackend.Controller;

import com.example.krishivaanibackend.service.SpeechService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/voice")
@CrossOrigin(origins = "*")
public class VoiceController {

    private final SpeechService speechService;

    public VoiceController(SpeechService speechService) {
        this.speechService = speechService;
    }

    // POST /api/v1/voice/transcribe
    // Form-data: audio (file), language (optional, default "hi")
    @PostMapping("/transcribe")
    public ResponseEntity<Map<String, String>> transcribe(
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam(value = "language", defaultValue = "hi") String language) {

        if (audioFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Empty file"));
        }
        String transcription = speechService.transcribe(audioFile, language);
        return ResponseEntity.ok(Map.of("transcription", transcription, "language", language));
    }
}
```

---

## Step 6 — Start the App

Set env vars in your terminal (or IntelliJ Run Config):

```bash
export DB_URL="jdbc:postgresql://ep-wispy-tree-b32wsb4i-pooler.c-4.ap-southeast-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require"
export DB_USERNAME="neondb_owner"
export DB_PASSWORD="npg_tHIY8aNos4jG"

cd backend
./mvnw spring-boot:run
```

---

## Step 7 — Test with curl

```bash
# Health check
curl http://localhost:8080/api/v1/health

# Submit a question
curl -X POST http://localhost:8080/api/v1/questions \
  -H "Content-Type: application/json" \
  -d '{"content": "Which fertilizer is best for wheat?", "language": "en"}'

# Get all questions
curl http://localhost:8080/api/v1/questions
```

---

## Mark KV-025 as DONE when
- [ ] App starts without errors
- [ ] `/api/v1/health` returns OK
- [ ] POST + GET `/api/v1/questions` works and data saves to Neon DB
