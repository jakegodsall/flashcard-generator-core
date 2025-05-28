package com.jakegodsall.models.flashcards.components;

import com.jakegodsall.utils.StringUtils;

/**
 * Component representing a sentence in the target language.
 */
public class TargetLanguageSentence implements FlashcardComponent {
    private static final String DESCRIPTION = "A sentence in the target language using the target word";
    private static final String JSON_STRUCTURE = StringUtils.createJsonComponent("targetSentence", "<sentence in target language using the target word>");
    private static final String CSV_COLUMN = "targetSentence";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public String getJsonStructure() {
        return JSON_STRUCTURE;
    }

    @Override
    public String getCsvColumnName() {
        return CSV_COLUMN;
    }
} 