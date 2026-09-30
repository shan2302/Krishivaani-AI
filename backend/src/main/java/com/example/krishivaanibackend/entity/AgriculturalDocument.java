package com.example.krishivaanibackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents an agricultural knowledge document stored for RAG retrieval.
 */
@Entity
@Table(name = "agricultural_documents")
public class AgriculturalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    /** Full document content used for embedding / RAG */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** Category tag (e.g. "pest-control", "soil-health", "irrigation") */
    @Column(length = 100)
    private String category;

    /** Source / citation for the document */
    @Column(length = 500)
    private String source;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ── Constructors ──────────────────────────────────────────────────────────

    public AgriculturalDocument() {}

    public AgriculturalDocument(String title, String content, String category, String source) {
        this.title    = title;
        this.content  = content;
        this.category = category;
        this.source   = source;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "AgriculturalDocument{id=" + id + ", title='" + title + "', category='" + category + "'}";
    }
}
