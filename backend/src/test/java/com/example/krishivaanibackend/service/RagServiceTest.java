package com.example.krishivaanibackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RagServiceTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    private RagService ragService;

    @BeforeEach
    void setUp() {
        ragService = new RagService(vectorStore, chatClient);
    }

    private void mockChatClientFlow(String llmOutput) {
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(any(String.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(llmOutput);
    }

    @Test
    void answerWithRag_whenDocumentsFound_augmentsPromptWithContext() {
        Document doc1 = new Document("Wheat rust is treated with Propiconazole 25 EC at 1 ml/litre.",
                Map.of("title", "Wheat Rust Control"));
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc1));

        mockChatClientFlow("Apply Propiconazole 25 EC at 1 ml/L.");

        String answer = ragService.answerWithRag("How to treat wheat rust?", "en");

        assertThat(answer).isEqualTo("Apply Propiconazole 25 EC at 1 ml/L.");

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestSpec).user(promptCaptor.capture());
        String capturedPrompt = promptCaptor.getValue();

        assertThat(capturedPrompt).contains("Wheat rust is treated with Propiconazole 25 EC");
        assertThat(capturedPrompt).contains("How to treat wheat rust?");
    }

    @Test
    void answerWithRag_whenNoDocumentsFound_usesFallbackContext() {
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of());

        mockChatClientFlow("General advice for treating rust.");

        String answer = ragService.answerWithRag("Unknown crop disease", "en");

        assertThat(answer).isEqualTo("General advice for treating rust.");

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestSpec).user(promptCaptor.capture());
        assertThat(promptCaptor.getValue()).contains("No relevant documents found");
    }

    @Test
    void answerWithRag_devanagariScript_setsHindiLanguageInstruction() {
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of());
        mockChatClientFlow("गेहूं के रतुआ का उपचार...");

        ragService.answerWithRag("गेहूं के रतुआ का इलाज कैसे करें?", "hi");

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestSpec).user(promptCaptor.capture());
        assertThat(promptCaptor.getValue()).contains("कृपया अपना संपूर्ण उत्तर केवल और केवल सरल, स्पष्ट हिंदी");
    }

    @Test
    void answerWithRag_kannadaScript_setsKannadaLanguageInstruction() {
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of());
        mockChatClientFlow("ಗೋಧಿ ತುಕ್ಕು ರೋಗ ಚಿಕಿತ್ಸೆ...");

        ragService.answerWithRag("ಗೋಧಿ ರೋಗ ಪರಿಹಾರ", "kn");

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestSpec).user(promptCaptor.capture());
        assertThat(promptCaptor.getValue()).contains("ದಯವಿಟ್ಟು ನಿಮ್ಮ ಸಂಪೂರ್ಣ ಉತ್ತರವನ್ನು ಸರಳ ಕನ್ನಡದಲ್ಲಿ ನೀಡಿ");
    }

    @Test
    void answerWithRag_whenLlmReturnsBlank_returnsFallbackMessage() {
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of());
        mockChatClientFlow("   ");

        String answer = ragService.answerWithRag("Some question", "en");

        assertThat(answer).contains("unable to generate a answer");
    }
}
