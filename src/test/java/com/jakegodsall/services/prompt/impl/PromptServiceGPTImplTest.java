package com.jakegodsall.services.prompt.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakegodsall.config.LanguageConfig;
import com.jakegodsall.models.Language;
import com.jakegodsall.models.Options;
import com.jakegodsall.models.enums.FlashcardType;
import com.jakegodsall.services.prompt.PromptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PromptServiceGPTImplTest {
    private PromptService promptService;
    private Language language;
    private Options options;

    @BeforeEach
    public void setUp() {
        promptService = new PromptServiceGPTImpl();
        language = LanguageConfig.getLanguage("es");
        options = new Options();
    }

    @Test
    public void testGenerateRequestBody() throws JsonProcessingException {
        String prompt = "Test prompt";
        String requestBody = promptService.generateRequestBody(prompt);

        ObjectMapper mapper = new ObjectMapper();
        var jsonNode = mapper.readTree(requestBody);

        assertEquals("gpt-3.5-turbo", jsonNode.get("model").asText());
        assertEquals(500, jsonNode.get("max_tokens").asInt());

        var messages = jsonNode.get("messages");
        assertTrue(messages.isArray());
        assertEquals(1, messages.size());

        var message = messages.get(0);
        assertEquals("user", message.get("role").asText());
        assertEquals(prompt, message.get("content").asText());
    }

    @Test
    public void testGeneratePromptForWordFlashcard() {
        String targetWord = "manzana";
        FlashcardType flashcardType = FlashcardType.WORD;

        String prompt = promptService.generatePrompt(targetWord, flashcardType, language, options);

        assertTrue(prompt.contains("Given a word in a target language generate the following JSON"));
        assertTrue(prompt.contains("The word is manzana and the target language is Spanish."));
    }

    @Test
    public void testGeneratePromptForSentenceFlashcard() {
        String targetWord = "El gato duerme";
        FlashcardType flashcardType = FlashcardType.SENTENCE;

        String prompt = promptService.generatePrompt(targetWord, flashcardType, language, options);

        assertTrue(prompt.contains("Given a word in a target language generate the following JSON"));
        assertTrue(prompt.contains("The word is El gato duerme and the target language is Spanish."));
    }

    @Test
    public void testGenerateBatchPrompt() {
        List<String> targetWords = List.of("manzana", "libro", "casa");
        FlashcardType flashcardType = FlashcardType.WORD;

        String prompt = promptService.generateBatchPrompt(targetWords, flashcardType, language, options);

        assertTrue(prompt.contains("Given the following words in Spanish, generate the following JSON for each word."));
        assertTrue(prompt.contains("- manzana"));
        assertTrue(prompt.contains("- libro"));
        assertTrue(prompt.contains("- casa"));
    }

    @Test
    public void testGeneratePromptForSubsequentWord() {
        String targetWord = "perro";
        String prompt = promptService.generatePromptForSubsequentWord(targetWord);

        assertEquals("The next word is perro", prompt);
    }

    @Test
    public void testGenerateRequestBodyForInvalidJson() {
        String invalidPrompt = "{This is not valid JSON}";
        assertThrows(JsonProcessingException.class, () -> {
            promptService.generateRequestBody(invalidPrompt);
        });
    }

    @Test
    public void testGeneratePromptWithNullValues() {
        assertThrows(NullPointerException.class, () -> {
            promptService.generatePrompt(null, null, null, null);
        });
    }
}