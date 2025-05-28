package com.jakegodsall.models.flashcards.components;

import com.jakegodsall.utils.StringUtils;

/**
 * Component representing a sentence in the source language.
 */
public class SourceLanguageSentence implements FlashcardComponent {
    private static final String DESCRIPTION = "A sentence in the source language that uses the SOURCE LANGUAGE translation of the target word (NOT the target word itself)";
    private static final String JSON_STRUCTURE = StringUtils.createJsonComponent("sourceSentence", "<sentence in source language using the TRANSLATED word from source language, never the target word>");
    private static final String CSV_COLUMN = "sourceSentence";

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