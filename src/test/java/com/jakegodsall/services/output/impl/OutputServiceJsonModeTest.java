package com.jakegodsall.services.output.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakegodsall.models.flashcards.Flashcard;
import com.jakegodsall.services.output.OutputService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OutputServiceJsonModeTest {
    private OutputService outputService;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        outputService = new OutputServiceJsonMode(objectMapper);
    }

    @Test
    public void serialiseToOutputFormat_validInput() throws JsonProcessingException {
        List<Flashcard> testFlashcards = generateDummyFlashcardList();

        String result = outputService.serialiseToOutputFormat(testFlashcards);
        JsonNode actual = objectMapper.readTree(result);
        JsonNode expected = objectMapper.readTree("[{\"sourceWord\":\"book\",\"targetWord\":\"libro\",\"exampleTargetSentence\":\"El libro.\"},{\"sourceWord\":\"car\",\"targetWord\":\"coche\",\"exampleTargetSentence\":\"El coche.\"}]");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void serialiseToOutputFormat_emptyList() throws JsonProcessingException {
        List<Flashcard> testFlashcards = new ArrayList<>();

        String result = outputService.serialiseToOutputFormat(testFlashcards);

        assertThat(result).isEmpty();
    }

    private List<Flashcard> generateDummyFlashcardList() {
        List<Flashcard> flashcards = new ArrayList<>();
        flashcards.add(new Flashcard(Map.of(
                "sourceWord", "book",
                "targetWord", "libro",
                "exampleTargetSentence", "El libro."
        )));
        flashcards.add(new Flashcard(Map.of(
                "sourceWord", "car",
                "targetWord", "coche",
                "exampleTargetSentence", "El coche."
        )));
        return flashcards;
    }
}