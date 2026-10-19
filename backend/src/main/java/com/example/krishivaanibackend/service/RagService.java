package com.example.krishivaanibackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import org.springframework.ai.document.Document;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private static final Logger log = LoggerFactory.getLogger(RagService.class);
    private static final int TOP_K = 4; // Number of top documents to retrieve
    private static final double SIMILARITY_THRESHOLD = 0.3; // Similarity threshold for filtering documents

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public RagService(VectorStore vectorStore, ChatClient chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
    }

    public String answerWithRag(String query, String language){
//        1. Retrieve similar documents
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(TOP_K)
                        .similarityThreshold(SIMILARITY_THRESHOLD)
                        .build()
        );
        log.info("RAG retrieval: query='{}', docs_found={}", query, docs.size());
        for (Document doc : docs) {
            log.info("Retrieved document: title='{}', score={}", doc.getMetadata().get("title"), doc.getScore());
        }

//        2. Build context string
        String context = docs.isEmpty() ? "" :
                docs.stream().map(d -> "* "+d.getText())
                        .collect(Collectors.joining("\n\n"));

//        3. Language Instruction
        String langInstruction = resolveLanguageInstruction(query,language);

//        4. Buold augumented prompt
        String contextBlock = docs.isEmpty()
                ? "(No relevant documents found. Answer from general knowledge but note the limitation.)"
                : context;

        String prompt = String.format("""
                Language Instruction: 
                %s
                
                You are KrishiVaani AI, a trusted agricultural assistant for Indian farmers.
                Answer ONLY based on the provided context. If context is insufficient, say so clearly.
                
                Formatting Guidelines:
                - Use bullet points (*) and numbered steps.
                - Do NOT use markdown tables or pipe symbols ().
                - Keep chemical/fertilizer terms understandable for farmers.
                
                Retrieved Agricultural Context:
                %s
                
                Farmer's Question:
                %s
                
                """,langInstruction,contextBlock,query);

//        5. Call LLM
        String response = chatClient.prompt().user(prompt).call().content();
        if(response == null || response.isBlank()){
            return "KrishiVaani AI was unable to generate a answer. Please rephrase your question or try again later.";
        }
        return response.trim();

    }

    private String resolveLanguageInstruction(String query, String language){
        String target = language != null ? language.toLowerCase().trim() : "en";
        if(query !=null && query.codePoints().anyMatch(
                cp -> Character.UnicodeBlock.of(cp) == Character.UnicodeBlock.DEVANAGARI
        ))
            target  = "hi";
        else if(query !=null && query.codePoints().anyMatch(
                cp -> Character.UnicodeBlock.of(cp) == Character.UnicodeBlock.KANNADA
        ))
            target = "kn";

        return switch (target){
            case "hi" -> "कृपया अपना संपूर्ण उत्तर केवल और केवल सरल, स्पष्ट हिंदी (देवनागरी लिपि) में दें।";
            case "kn" -> "ದಯವಿಟ್ಟು ನಿಮ್ಮ ಸಂಪೂರ್ಣ ಉತ್ತರವನ್ನು ಸರಳ ಕನ್ನಡದಲ್ಲಿ ನೀಡಿ.";
            default -> "Please provide your complete answer in simple, clear English.";
        };
    }
}
