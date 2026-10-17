package com.example.krishivaanibackend.service;

public interface AIService {
    /**
     * Generated an agricultural response using the Groq LLM.
     * @param content Farmers query
     * @param language Target Language (en,hi,kn)
     * @return AI generated response
     */
    String generateAnswer(String content, String language);

}
