package com.jakegodsall.services.prompt.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jakegodsall.models.Language;
import com.jakegodsall.models.enums.FlashcardType;
import com.jakegodsall.models.flashcards.SentenceFlashcard;
import com.jakegodsall.models.flashcards.WordFlashcard;
import com.jakegodsall.services.prompt.PromptService;
import com.jakegodsall.models.Options;


/**
 * Implementation of PromptService for GPT-specific prompt generation.
 */
public class PromptServiceGPTImpl implements PromptService {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String generateRequestBody(String prompt) throws JsonProcessingException {
        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("max_tokens", 500);

        ArrayNode messages = mapper.createArrayNode();
        ObjectNode userMessage = mapper.createObjectNode();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        messages.add(userMessage);
        requestBody.put("messages", messages);

        return mapper.writeValueAsString(requestBody);
    }

    @Override
    public String generatePrompt(String targetWord, FlashcardType flashcardType, Language language, Options options) {
        return generateBasePrompt() + getDescriptionOfContent(flashcardType) + getFlashcardStructure(flashcardType) + "The word is " + targetWord + " and the target language is " + language.getName() + ".\n";
    }

    @Override
    public String generatePromptForMultipleFlashcards(String targetWord, FlashcardType flashcardType, Language language, Options options, int count) {
        return "Generate " + count + " different flashcards as a JSON array. IMPORTANT: Each flashcard MUST use EXACTLY the word \"" + targetWord + "\" - do not use synonyms or related words.\n" +
               generateBasePrompt() + getDescriptionOfContent(flashcardType) + 
               "The structure for each flashcard in the array should be:\n" + 
               getFlashcardStructure(flashcardType) + 
               "The word is \"" + targetWord + "\" and the target language is " + language.getName() + ".\n" +
               "Each flashcard should use different example sentences and translations while maintaining accuracy.\n" +
               "Remember: Every flashcard MUST use the exact word \"" + targetWord + "\" - not synonyms, not related words.\n";
    }

    @Override
    public String generatePromptForSubsequentWord(String targetWord) {
        return "The next word is " + targetWord;
    }

    private String generateBasePrompt() {
        return "Given a word in a target language generate the following JSON.\n\"The JSON should include:\n";
    }

    private String getDescriptionOfContent(FlashcardType flashcardType) {
        return switch (flashcardType) {
            case WORD -> WordFlashcard.DESCRIPTION_OF_CONTENT;
            case SENTENCE -> SentenceFlashcard.DESCRIPTION_OF_CONTENT;
            default -> throw new IllegalArgumentException("Unsupported FlashcardType: " + flashcardType);
        };
    }

    private String getFlashcardStructure(FlashcardType flashcardType) {
        return switch (flashcardType) {
            case WORD -> WordFlashcard.JSON_STRUCTURE_FOR_PROMPT;
            case SENTENCE -> SentenceFlashcard.JSON_STRUCTURE_FOR_PROMPT;
            default -> throw new IllegalArgumentException("Unsupported FlashcardType: " + flashcardType);
        };
    }
}
