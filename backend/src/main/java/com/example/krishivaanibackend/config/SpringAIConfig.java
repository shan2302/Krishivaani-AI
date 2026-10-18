package com.example.krishivaanibackend.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAIConfig {

    private static final String KRISHIVAANI_SYSTEM_PROMPT = """
            You are KrishiVaani AI, an expert and empathetic agricultural advisor for Indian farmers.
                    Provide practical, safe, step-by-step advice on crop management, fertilizers, pest control,
                    and irrigation. Always respond in the requested language (English, Hindi, or Kannada).
            """;
    @Bean
    public ChatClient chatClient(ChatModel chatModel){
        return ChatClient.builder(chatModel)
                .defaultSystem(KRISHIVAANI_SYSTEM_PROMPT)
                .build();
    }

    /**
     * Interceptor to fix missing 'index' field in Gemini's OpenAI-compatible embedding responses.
     * The official OpenAI Java SDK requires 'index' on each Embedding object and throws
     * OpenAIInvalidDataException: `index` is not set if it is absent.
     */
    @Bean
    public org.springframework.ai.openai.http.okhttp.OpenAiHttpClientBuilderCustomizer geminiEmbeddingIndexFixer() {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        return builder -> builder.interceptor(chain -> {
            okhttp3.Response response = chain.proceed(chain.request());
            if (response.isSuccessful() && response.body() != null) {
                okhttp3.MediaType mediaType = response.body().contentType();
                String contentTypeStr = mediaType != null ? mediaType.toString() : "";
                if (contentTypeStr.contains("application/json")) {
                    String bodyString = response.body().string();
                    if (bodyString.contains("\"embedding\"")) {
                        try {
                            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(bodyString);
                            if (root.isObject()) {
                                com.fasterxml.jackson.databind.node.ObjectNode rootObj =
                                        (com.fasterxml.jackson.databind.node.ObjectNode) root;

                                // 1. Fix missing 'model' field
                                if (!rootObj.has("model")) {
                                    rootObj.put("model", "gemini-embedding-2-preview");
                                }

                                // 2. Fix missing 'usage' field required by OpenAI SDK
                                if (!rootObj.has("usage")) {
                                    com.fasterxml.jackson.databind.node.ObjectNode usage = rootObj.putObject("usage");
                                    usage.put("prompt_tokens", 0L);
                                    usage.put("total_tokens", 0L);
                                }

                                // 3. Fix missing 'index' field in each data array item
                                if (rootObj.has("data") && rootObj.get("data").isArray()) {
                                    com.fasterxml.jackson.databind.node.ArrayNode dataArray =
                                            (com.fasterxml.jackson.databind.node.ArrayNode) rootObj.get("data");
                                    for (int i = 0; i < dataArray.size(); i++) {
                                        com.fasterxml.jackson.databind.JsonNode item = dataArray.get(i);
                                        if (item.isObject() && !item.has("index")) {
                                            ((com.fasterxml.jackson.databind.node.ObjectNode) item).put("index", i);
                                        }
                                    }
                                }

                                String fixedBody = mapper.writeValueAsString(rootObj);
                                okhttp3.ResponseBody newBody = okhttp3.ResponseBody.create(fixedBody, mediaType);
                                return response.newBuilder().body(newBody).build();
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    okhttp3.ResponseBody newBody = okhttp3.ResponseBody.create(bodyString, mediaType);
                    return response.newBuilder().body(newBody).build();
                }
            }
            return response;
        });
    }
}
