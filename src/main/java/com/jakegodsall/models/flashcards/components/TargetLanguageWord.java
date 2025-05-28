package com.jakegodsall.models.flashcards.components;

import com.jakegodsall.utils.StringUtils;

/**
 * Component representing a word in the target language.
 */
public class TargetLanguageWord implements FlashcardComponent {
    private static final String DESCRIPTION = "The exact TARGET LANGUAGE word as provided (the original word given, not translated)";
    private static final String JSON_STRUCTURE = StringUtils.createJsonComponent("targetWord", "<exact target word provided, unchanged>");
    private static final String CSV_COLUMN = "targetWord";

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