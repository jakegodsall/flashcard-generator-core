package com.jakegodsall.models.flashcards.components;

/**
 * Interface representing a component of a flashcard.
 * Each component provides its own description and JSON structure for prompt generation.
 */
public interface FlashcardComponent {
    /**
     * Gets the description of what this component represents.
     * This is used in the prompt to explain what the component is for.
     *
     * @return the description of the component
     */
    String getDescription();

    /**
     * Gets the JSON structure for this component.
     * This defines how the component should be formatted in the JSON response.
     *
     * @return the JSON structure as a string
     */
    String getJsonStructure();

    /**
     * Gets the CSV column name for this component.
     * This is used when exporting flashcards to CSV format.
     *
     * @return the CSV column name
     */
    String getCsvColumnName();
} 