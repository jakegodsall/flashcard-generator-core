package com.jakegodsall.services.prompt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jakegodsall.models.Language;
import com.jakegodsall.models.enums.FlashcardType;
import com.jakegodsall.models.Options;

/**
 * Interface for prompt generator to create prompts for the OpenAI API.
 */
public interface PromptService {
    /**
     * Generates the request body for the OpenAI API call using a provided prompt.
     *
     * @param prompt the prompt to include in the request.
     * @return the JSON string representing the request body.
     * @throws JsonProcessingException if an error occurs while processing JSON.
     */
    String generateRequestBody(String prompt) throws JsonProcessingException;

    /**
     * Generates a prompt for the API based on the provided target word, flashcard type, language, and additional options.
     * This prompt is used to structure the request sent to the API, determining the format of the returned data (e.g.,
     * word-based or sentence-based flashcards).
     *
     * <p>The generated prompt will be customized depending on the {@link FlashcardType},
     * allowing flexibility to support various types of flashcards, such as word flashcards or sentence flashcards.
     * The structure of the prompt ensures that the returned data follows the correct format expected for that specific flashcard type.</p>
     *
     * @param targetWord the word in the target language that will be used to generate the flashcard.
     * @param flashcardType the type of flashcard to generate, as defined by the {@link FlashcardType} enumeration (e.g., WORD, SENTENCE).
     * @param language the language in which the flashcard content will be generated.
     * @param options additional options that influence the generation of the prompt, such as difficulty level or specific grammatical features.
     * @return the generated prompt as a {@code String}, which will be sent to the API.
     */
    String generatePrompt(String targetWord, FlashcardType flashcardType, Language language, Options options);

    /**
     * Generates a prompt for multiple flashcards based on a single target word.
     *
     * @param targetWord the word in the target language that will be used to generate multiple flashcards.
     * @param flashcardType the type of flashcard to generate.
     * @param language the language in which the flashcard content will be generated.
     * @param options additional options that influence the generation of the prompt.
     * @param count the number of flashcards to generate.
     * @return the generated prompt as a {@code String}, which will be sent to the API.
     */
    String generatePromptForMultipleFlashcards(String targetWord, FlashcardType flashcardType, Language language, Options options, int count);

    /**
     * Creates a simple prompt for the next word in a sequence.
     *
     * <p>This method generates a basic prompt that includes the given word, typically used for
     * processing or highlighting the next word in a sequence.</p>
     *
     * @param targetWord the word to be included in the prompt.
     * @return a string containing the prompt for the next word.
     */
    public String generatePromptForSubsequentWord(String targetWord);
}