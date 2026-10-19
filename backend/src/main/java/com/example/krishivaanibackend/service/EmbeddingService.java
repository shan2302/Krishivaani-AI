package com.example.krishivaanibackend.service;

import com.example.krishivaanibackend.entity.AgriculturalDocument;
import com.example.krishivaanibackend.repository.AgriculturalDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.List;

@Service
@Transactional
public class EmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingService.class);

    private final AgriculturalDocumentRepository documentRepository;
    private final VectorStore vectorStore;

    public EmbeddingService(AgriculturalDocumentRepository documentRepository, VectorStore vectorStore){
        this.documentRepository = documentRepository;
        this.vectorStore = vectorStore;
    }

    public AgriculturalDocument saveDocument(String title, String content,
                                             String category, String source){
        return documentRepository.save(new AgriculturalDocument(title, content, category, source));
    }

    @Transactional(readOnly = true)
    public List<AgriculturalDocument> findByCategory(String category){
        return documentRepository.findByCategory(category);
    }

    // Embed a single document and upsert into PgVector
    public void embedAndStore(AgriculturalDocument doc){
        Document aiDoc = new Document(doc.getContent(),
                Map.of("id", String.valueOf(doc.getId()),
                        "title", doc.getTitle(),
                        "category" , doc.getCategory() != null ? doc.getCategory() : "",
                        "source" , doc.getSource() != null ? doc.getSource() : ""
                ));
        vectorStore.add(List.of(aiDoc));
        log.info("Embedded and stored single document id={} into PgVector", doc.getId());
    }

    // Bulk embed ALL documents in DB - PgVector (with auto-seeding if empty)
    public int embedAll(){
        if (documentRepository.count() == 0) {
            log.info("agricultural_documents table is empty. Auto-seeding initial documents...");
            documentRepository.saveAll(List.of(
                new AgriculturalDocument(
                    "Wheat Rust Control",
                    "Wheat rust (Puccinia) is controlled by Propiconazole (Tilt 25 EC) at 1 ml/litre. Spray at flag-leaf stage. Repeat after 15 days if needed.",
                    "pest-control",
                    "ICAR Wheat Disease Management Guide"
                ),
                new AgriculturalDocument(
                    "Drip Irrigation for Cotton",
                    "Cotton needs 600-700 mm water/season. Drip irrigation saves 30-40%. Space laterals 60 cm apart. Irrigate at 0.8 ETc.",
                    "irrigation",
                    "TNAU Agritech Portal"
                ),
                new AgriculturalDocument(
                    "Soil pH Correction",
                    "If soil pH < 6.0, apply 2-4 tonnes lime/hectare before sowing. Use balanced NPK per soil health card.",
                    "soil-health",
                    "Government of India Soil Health Card Scheme"
                ),
                new AgriculturalDocument(
                    "Paddy Blast Disease",
                    "Rice blast (Magnaporthe oryzae) causes leaf and neck lesions. Apply Tricyclazole 75 WP at 0.6 g/litre at tillering. Avoid excess nitrogen.",
                    "pest-control",
                    "ICAR Rice Knowledge Management Portal"
                )
            ));
            log.info("Seeded 4 default agricultural documents.");
        }

        List<AgriculturalDocument> allDocs = documentRepository.findAll();
        log.info("Found {} documents in database to embed into PgVector", allDocs.size());

        List<Document> aiDocs = allDocs.stream()
                .map(doc -> new Document(doc.getContent(),
                        Map.of("id", String.valueOf(doc.getId()),
                                "title", doc.getTitle(),
                                "category" , doc.getCategory() != null ? doc.getCategory() : "",
                                "source" , doc.getSource() != null ? doc.getSource() : ""
                        )
                )).toList();

        vectorStore.add(aiDocs);
        log.info("Successfully added {} documents into PgVector store", aiDocs.size());
        return aiDocs.size();
    }
}
