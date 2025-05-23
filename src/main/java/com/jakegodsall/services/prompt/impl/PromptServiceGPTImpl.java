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
    public String generatePrompt(String targetWord, FlashcardType flashcardType, Language sourceLanguage, Language targetLanguage, Options options) {
        return generateBasePrompt() + 
               getDescriptionOfContent(flashcardType) + 
               getFlashcardStructure(flashcardType) + 
               "The word in the target language is \"" + targetWord + "\" and the target language is " + targetLanguage.getName() + ".\n" +
               getFormattingRules();
    }

    @Override   
    public String generatePromptForMultipleFlashcards(String targetWord, FlashcardType flashcardType, Language sourceLanguage, Language targetLanguage, Options options, int count) {
        StringBuilder prompt = new StringBuilder();
        
        // Initial warning about target word usage
        if (flashcardType == FlashcardType.SENTENCE) {
            prompt.append("⚠️ CRITICAL REQUIREMENT: When generating sentences, you must NEVER use the word \"")
                  .append(targetWord)
                  .append("\" in the source language sentences. Instead, use its translation or rephrase the sentence completely.\n\n");
        }

        prompt.append("You are a language learning assistant.\n")
              .append("You are given a word in the target language (").append(targetLanguage.getName()).append(") and you need to generate flashcards for it.\n")
              .append("First, translate the target word \"").append(targetWord).append("\" to the source language (").append(sourceLanguage.getName()).append(").\n")
              .append("This translation will be used consistently across all flashcards.\n\n");
        
        prompt.append("Generate ")
              .append(count)
              .append(" different flashcards as a JSON array. IMPORTANT: Each flashcard MUST use EXACTLY the word \"")
              .append(targetWord)
              .append("\" in the target language - do not use synonyms or related words.\n")
              .append(generateBasePrompt())
              .append(getDescriptionOfContent(flashcardType))
              .append("The structure for each flashcard in the array should be:\n")
              .append(getFlashcardStructure(flashcardType))
              .append(giveExampleSentence())
              .append("The word in the target language is \"")
              .append(targetWord)
              .append("\" (").append(targetLanguage.getName()).append(") and needs to be translated to ")
              .append(sourceLanguage.getName())
              .append(".\n");

        prompt.append("Each flashcard should use different example sentences and translations while maintaining accuracy.\n")
              .append("Remember: Every flashcard MUST use the exact word \"")
              .append(targetWord)
              .append("\" in the target language - not synonyms, not related words.\n")
              .append("The source language translation of this word should be consistent across all flashcards.\n")
              .append(getFormattingRules());

        return prompt.toString();
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

    private String getFormattingRules() {
        return "Please follow these formatting rules:\n" +
               "1. First, translate the target word to the source language and use this translation consistently across all flashcards\n" +
               "2. The source word and target word should be in lowercase\n" +
               "3. Example sentences must start with a capital letter and end with a period\n" +
               "4. Ensure proper sentence structure and punctuation in all example sentences\n" +
               "5. STRICT RULE FOR SENTENCE FLASHCARDS: The target word MUST NEVER appear in the source language sentence - ALWAYS use its translation or completely different phrasing\n" +
               "6. Double-check every source sentence to ensure it does not contain the target word in any form\n";
    }

    private String giveExampleSentence() {
        return "I will now give you an example sentence.\n" +
               "Let's say the source language is English and the target language is Spanish.\n" +
               "The source word is \"book\" and the target word is \"libro\".\n" +
               "The example sentence is \"The book is on the table\".\n" +
               "The target sentence is \"El libro está en la mesa\".\n" +
               "The source sentence is \"The book is on the table\".\n" +
               "The target sentence is \"El libro está en la mesa\".\n";
    }
}
