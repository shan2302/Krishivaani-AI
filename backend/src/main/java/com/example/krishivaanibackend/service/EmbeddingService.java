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

    public EmbeddingService(AgriculturalDocumentRepository documentRepository){
        this.documentRepository = documentRepository;
    }

    public AgriculturalDocument saveDocument(String title, String content,
                                             String category, String source){
        return documentRepository.save(new AgriculturalDocument(title, content, category, source));
    }

    @Transactional(readOnly = true)
    public List<AgriculturalDocument> findByCategory(String category){
        return documentRepository.findByCategory(category);
    }
//    TODO: add Vector embedding methods when pgvector is set up
}
