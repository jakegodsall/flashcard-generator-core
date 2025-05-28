package com.jakegodsall.models.flashcards;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for flashcards that uses a component-based system.
 * Each flashcard stores its values in a map where the keys are the component CSV column names.
 */
@Getter
@JsonSerialize(using = FlashcardSerializer.class)
public class Flashcard {
    private final Map<String, String> componentValues;

    /**
     * Creates a new Flashcard with the given component values.
     *
     * @param componentValues a map of component CSV column names to their values
     */
    public Flashcard(Map<String, String> componentValues) {
        this.componentValues = new HashMap<>(componentValues);
    }

    /**
     * Gets the value for a specific component by its CSV column name.
     *
     * @param columnName the CSV column name of the component
     * @return the value of the component, or null if not found
     */
    public String getComponentValue(String columnName) {
        return componentValues.get(columnName);
    }

    /**
     * Returns a string representation of the flashcard,
     * combining all component values.
     *
     * @return a string representation of the flashcard content
     */
    @Override
    public String toString() {
        return String.join(" - ", componentValues.values());
    }
}
