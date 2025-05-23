package com.jakegodsall.services.output.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakegodsall.models.flashcards.Flashcard;
import com.jakegodsall.models.flashcards.WordFlashcard;
import com.jakegodsall.services.output.OutputService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OutputServiceJsonModeTest {
    private OutputService outputService;

    @BeforeEach
    public void setUp() {
        outputService = new OutputServiceJsonMode(new ObjectMapper());

    }

    @Test
    public void serialiseToOutputFormat_validInput() throws JsonProcessingException {
        List<Flashcard> testFlashcards = generateDummyFlashcardList();

        String result = outputService.serialiseToOutputFormat(testFlashcards);

        assertThat(result)
                .isEqualTo("[{\"sourceWord\":\"book\",\"targetWord\":\"libro\",\"exampleTargetSentence\":\"El libro.\"},{\"sourceWord\":\"car\",\"targetWord\":\"coche\",\"exampleTargetSentence\":\"El coche.\"}]");
    }

    @Test
    public void serialiseToOutputFormat_emptyList() throws JsonProcessingException {
        List<Flashcard> testFlashcards = new ArrayList<>();

        String result = outputService.serialiseToOutputFormat(testFlashcards);

        assertThat(result).isEmpty();
    }

    private List<Flashcard> generateDummyFlashcardList() {
        List<Flashcard> flashcards = new ArrayList<>();
        flashcards.add(WordFlashcard.builder()
                .sourceWord("book")
                .targetWord("libro")
                .exampleTargetSentence("El libro.")
                .build());
        flashcards.add(WordFlashcard.builder()
                .sourceWord("car")
                .targetWord("coche")
                .exampleTargetSentence("El coche.")
                .build());
        return flashcards;
    }
}