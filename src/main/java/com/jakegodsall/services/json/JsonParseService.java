package com.jakegodsall.services.json;

import com.jakegodsall.models.enums.FlashcardType;
import com.jakegodsall.models.flashcards.Flashcard;

import java.io.IOException;
import java.util.List;

/**
 * Interface for JSON parser to handle parsing of JSON responses.
 */
public interface JsonParseService {
    /**
     * Parses the list of model names from the given JSON string.
     *
     * @param json the JSON string to parse.
     * @return a list of model names.
     * @throws IOException if an error occurs while parsing the JSON.
     */
    List<String> parseModels(String json) throws IOException;

    /**
     * Parses the API response body to create a flashcard based on the specified {@link FlashcardType}.
     *
     * <p>This method processes the JSON response and extracts relevant fields to construct
     * either an instance of a subclass of {@link Flashcard},
     * depending on the given {@code FlashcardType}. It ensures that the required fields are present
     * in the response and encapsulates them into the appropriate flashcard object.</p>
     *
     * @param responseBody the raw JSON response from the API.
     * @param flashcardType the type of flashcard to create, defined by the {@link FlashcardType} enum.
     * @return a parsed {@link Flashcard} object.
     */
    Flashcard parseFlashcard(String responseBody, FlashcardType flashcardType);

    /**
     * Parses multiple flashcards from a single API response.
     *
     * @param responseBody the raw JSON response from the API containing multiple flashcards.
     * @param flashcardType the type of flashcards to create, defined by the {@link FlashcardType} enum.
     * @return a list of parsed {@link Flashcard} objects.
     */
    List<Flashcard> parseMultipleFlashcards(String responseBody, FlashcardType flashcardType);
}
