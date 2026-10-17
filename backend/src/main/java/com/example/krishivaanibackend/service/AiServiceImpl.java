package com.example.krishivaanibackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiServiceImpl implements AIService{

    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);
    private final ChatClient chatClient;

    public AiServiceImpl(ChatClient chatClient){
        this.chatClient = chatClient;
    }

    @Override
    public String generateAnswer(String content, String language) {
        String trimmedContent = content != null ? content.trim() : "";

        // Auto-detect language if Devanagari or Kannada script is present
        String targetLang = language != null ? language.toLowerCase().trim() : "en";
        if (trimmedContent.codePoints().anyMatch(cp -> Character.UnicodeBlock.of(cp) == Character.UnicodeBlock.DEVANAGARI)) {
            targetLang = "hi";
        } else if (trimmedContent.codePoints().anyMatch(cp -> Character.UnicodeBlock.of(cp) == Character.UnicodeBlock.KANNADA)) {
            targetLang = "kn";
        }

        String languageInstruction = switch (targetLang) {
            case "hi" -> "कृपया अपना संपूर्ण उत्तर केवल और केवल सरल, स्पष्ट हिंदी (देवनागरी लिपि) में दें। उत्तर में अंग्रेजी का प्रयोग बिल्कुल न करें।";
            case "kn" -> "ದಯವಿಟ್ಟು ನಿಮ್ಮ ಸಂಪೂರ್ಣ ಉತ್ತರವನ್ನು ಸರಳ ಕನ್ನಡದಲ್ಲಿ ನೀಡಿ. ಇಂಗ್ಲಿಷ್ ಬಳಸಬೇಡಿ.";
            default -> "Please provide your answer in simple and clear English.";
        };

        String userPrompt = String.format(
                """
                Language Instruction:
                %s

                Formatting Guidelines:
                - Use structured, easy-to-read bullet points (•) and concise numbered steps.
                - Do NOT use markdown tables or pipe symbols (|), as they do not render well in mobile chat.
                - Keep technical chemical/fertilizer terms understandable for Indian farmers.

                Farmer's Query:
                %s
                """,
                languageInstruction,
                trimmedContent
        );

        try{
            log.info("Sending request to Groq (openai/gpt-oss-120b) for language: {}", language);
            String response = chatClient.prompt()
                    .user(userPrompt)
                    .call()
                    .content();

            if (response == null || response.isBlank()){
                log.warn("Groq returned empty response for query: {}",content);
                return "KrishiVaani AI was unable to generate an answer at this moment. Please rephrase your question.";
            }
            return response.trim();
        } catch (Exception e) {
            log.error("Error occurred while generating answer for query: {}", e.getMessage(),e);
            throw new RuntimeException("AI service is temporarily unavailable: " + e.getMessage(), e);
        }
    }
}
