package com.jakegodsall.models.flashcards.components;

import com.jakegodsall.utils.StringUtils;

/**
 * Component representing a word in the source language.
 */
public class SourceLanguageWord implements FlashcardComponent {
    private static final String DESCRIPTION = "The SOURCE LANGUAGE translation of the target word (translate the target word to source language)";
    private static final String JSON_STRUCTURE = StringUtils.createJsonComponent("sourceWord", "<target word translated into source language>");
    private static final String CSV_COLUMN = "sourceWord";

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