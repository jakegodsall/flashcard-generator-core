package com.jakegodsall.models.flashcards;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.*;
import com.jakegodsall.utils.StringUtils;

/**
 * The {@code WordFlashcard} class represents a flashcard containing a word
 * in both the native language (e.g., English) and the target language.
 * Additionally, it includes an example sentence in the target language.
 *
 * <p>This class is part of the flashcard system for language learning,
 * providing both word-level and sentence-level examples to help users
 * learn vocabulary in a practical context.</p>
 *
 * <p>It is annotated with Lombok for automatic getter, setter, builder,
 * and equality generation, as well as Jackson and OpenCSV annotations for
 * JSON and CSV handling, respectively.</p>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Builder
@JsonPropertyOrder({ "sourceWord", "targetWord", "exampleTargetSentence" })
public class WordFlashcard extends Flashcard {
    /**
     * A description of the flashcard's content,
     */
    public static final String DESCRIPTION_OF_CONTENT = "The word translated into the source language, the word itself, and a very basic sentence using the target word in the target language";

    /**
     * A JSON structure template for creating flashcards.
     * It uses the {@link StringUtils#createJsonComponent} utility to generate the components.
     */
    public static final String JSON_STRUCTURE_FOR_PROMPT = "{\n" +
        StringUtils.createJsonComponent("sourceWord", "<word translated into source language>") + ",\n" +
        StringUtils.createJsonComponent("targetWord", "<word in target language>") + ",\n" +
        StringUtils.createJsonComponent("targetSentence", "<sentence in target language using the target word>") + "\n" +
        "}\n";

    /**
     * The word in the source language.
     */
    @CsvBindByName(column = "sourceWord")
    @CsvBindByPosition(position = 0)
    String sourceWord;

    /**
     * The word in the target language.
     */
    @CsvBindByName(column = "targetWord")
    @CsvBindByPosition(position = 1)
    String targetWord;

    /**
     * An example sentence in the target language using the target word.
     */
    @CsvBindByName(column = "exampleTargetSentence")
    @CsvBindByPosition(position = 2)
    String exampleTargetSentence;

    /**
     * Returns a string representation of the flashcard,
     * combining the source word, target word, and example sentence.
     *
     * @return a string representation of the flashcard content.
     */
    @Override
    public String toString() {
        return sourceWord + " - " + targetWord + " - " + exampleTargetSentence;
    }
}
