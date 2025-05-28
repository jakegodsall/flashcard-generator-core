package com.jakegodsall.services.prompt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jakegodsall.models.Language;
import com.jakegodsall.models.flashcards.components.FlashcardComponent;
import com.jakegodsall.models.Options;
import java.util.List;

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
     * Generates a prompt for the API based on the provided target word, components, languages, and additional options.
     * This prompt is used to structure the request sent to the API, determining the format of the returned data.
     *
     * @param targetWord the word in the target language that will be used to generate the flashcard.
     * @param components the list of components to include in the flashcard.
     * @param sourceLanguage the language in which the source components will be generated.
     * @param targetLanguage the language in which the target components will be generated.
     * @param options additional options that influence the generation of the prompt.
     * @return the generated prompt as a {@code String}, which will be sent to the API.
     */
    String generatePrompt(String targetWord, List<FlashcardComponent> components, Language sourceLanguage, Language targetLanguage, Options options);

    /**
     * Generates a prompt for multiple flashcards based on a single target word.
     *
     * @param targetWord the word in the target language that will be used to generate multiple flashcards.
     * @param components the list of components to include in each flashcard.
     * @param sourceLanguage the language in which the source components will be generated.
     * @param targetLanguage the language in which the target components will be generated.
     * @param options additional options that influence the generation of the prompt.
     * @param count the number of flashcards to generate.
     * @return the generated prompt as a {@code String}, which will be sent to the API.
     */
    String generatePromptForMultipleFlashcards(String targetWord, List<FlashcardComponent> components, Language sourceLanguage, Language targetLanguage, Options options, int count);

    /**
     * Creates a simple prompt for the next word in a sequence.
     *
     * @param targetWord the word to be included in the prompt.
     * @return a string containing the prompt for the next word.
     */
    String generatePromptForSubsequentWord(String targetWord);
}