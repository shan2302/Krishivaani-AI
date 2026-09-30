package com.example.krishivaanibackend.repository;

import com.example.krishivaanibackend.entity.AgriculturalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for AgriculturalDocument entities.
 * Provides CRUD and custom query methods for the agricultural_documents table.
 */
@Repository
public interface AgriculturalDocumentRepository extends JpaRepository<AgriculturalDocument, Long> {

    /** Find documents by category (e.g. "pest-control", "soil-health") */
    List<AgriculturalDocument> findByCategory(String category);

    /** Full-text search by title keyword */
    List<AgriculturalDocument> findByTitleContainingIgnoreCase(String keyword);
}
