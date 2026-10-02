package com.example.krishivaanibackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a user agricultural question submitted to KrishiVaani AI.
 */
@Entity
@Table(
        name = "questions",
        indexes = {
                @Index(name = "idx_questions_language", columnList = "language"),
                @Index(name = "idx_questions_created_at", columnList = "created_at")
        }
)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** Language/locale of the question (e.g. "en", "hi", "ta") */
    @Column(length = 10)
    private String language;

    /** AI-generated answer stored for caching / audit */
    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt  = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ── Constructors ──────────────────────────────────────────────────────────

    public Question() {}

    public Question(String content, String language) {
        this.content  = content;
        this.language = language;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public String toString() {
        return "Question{id=" + id + ", language='" + language + "', content='" + content + "'}";
    }
}
