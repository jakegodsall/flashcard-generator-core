package com.jakegodsall.services.json.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakegodsall.models.enums.FlashcardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class JsonParseServiceGPTImplTest {

    private ObjectMapper objectMapper;
    private JsonParseServiceGPTImpl jsonParseService;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        jsonParseService = new JsonParseServiceGPTImpl(objectMapper);
    }

    // PARSE WORD FLASHCARD

    @Test
    public void parseWordFlashcard_missingNativeWordField() throws Exception {
        String responseBody = "{ \"choices\": [{ \"message\": { \"content\": " +
                "{ \"targetWord\": \"Hola\", \"targetSentence\": \"Hola, ¿cómo estás?\" }" +
                "} }] }";
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            jsonParseService.parseFlashcard(responseBody, FlashcardType.WORD);
        });
        assertEquals("Missing 'nativeWord' field in the JSON response", exception.getMessage());
    }

    @Test
    public void parseWordFlashcard_generalParsingException() throws Exception {
        String responseBody = "{ \"invalid-json\": \"invalid\" }";
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            jsonParseService.parseFlashcard(responseBody, FlashcardType.WORD);
        });
        assertEquals("Missing 'choices' field in the JSON response", exception.getMessage());
    }

    @Test
    public void parseWordFlashcard_missingContentInResponseBody_throwsNoSuchElementException() {
        String responseBody = "{ \"choices\": [] }"; // Simulates missing 'content' field

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            jsonParseService.parseFlashcard(responseBody, FlashcardType.WORD);
        });

        assertEquals("Missing 'choices' field in the JSON response", exception.getMessage());
    }

}