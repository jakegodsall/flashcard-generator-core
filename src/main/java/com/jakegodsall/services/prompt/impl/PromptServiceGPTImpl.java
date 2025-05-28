package com.jakegodsall.services.prompt.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jakegodsall.models.Language;
import com.jakegodsall.models.flashcards.components.FlashcardComponent;
import com.jakegodsall.models.flashcards.components.SourceLanguageSentence;
import com.jakegodsall.models.flashcards.components.TargetLanguageSentence;
import com.jakegodsall.models.flashcards.components.SourceLanguageWord;
import com.jakegodsall.models.flashcards.components.TargetLanguageWord;
import com.jakegodsall.services.prompt.PromptService;
import com.jakegodsall.models.Options;
import java.util.List;
import java.util.stream.Collectors;

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
    public String generatePrompt(String targetWord, List<FlashcardComponent> components, Language sourceLanguage, Language targetLanguage, Options options) {
        StringBuilder prompt = new StringBuilder();
        
        // Add critical language usage warnings
        prompt.append("🚨 CRITICAL LANGUAGE USAGE RULES 🚨\n");
        prompt.append("NEVER mix languages in the wrong components!\n");
        prompt.append("- SOURCE language components must ONLY use ").append(sourceLanguage.getName()).append("\n");
        prompt.append("- TARGET language components must ONLY use ").append(targetLanguage.getName()).append("\n");
        prompt.append("- The target word \"").append(targetWord).append("\" is in ").append(targetLanguage.getName()).append("\n");
        prompt.append("- You must translate \"").append(targetWord).append("\" to ").append(sourceLanguage.getName()).append(" first\n");
        prompt.append("- Use the translated word in ALL source language components\n");
        prompt.append("- Use the original word \"").append(targetWord).append("\" in ALL target language components\n\n");
        
        prompt.append(generateBasePrompt())
               .append(getComponentsDescription(components))
               .append(getLanguageSpecificInstructions(components, sourceLanguage, targetLanguage))
               .append(getComponentsJsonStructure(components))
               .append("The word in the target language is \"").append(targetWord).append("\" (").append(targetLanguage.getName())
               .append(") and needs to be translated to ").append(sourceLanguage.getName()).append(".\n")
               .append(getFormattingRules());
        
        return prompt.toString();
    }

    @Override   
    public String generatePromptForMultipleFlashcards(String targetWord, List<FlashcardComponent> components, Language sourceLanguage, Language targetLanguage, Options options, int count) {
        StringBuilder prompt = new StringBuilder();
        
        // Enhanced critical warnings
        prompt.append("🚨🚨🚨 ABSOLUTE CRITICAL LANGUAGE SEPARATION RULES 🚨🚨🚨\n");
        prompt.append("VIOLATION OF THESE RULES WILL RESULT IN UNUSABLE FLASHCARDS!\n\n");
        
        prompt.append("LANGUAGE ASSIGNMENT:\n");
        prompt.append("- Target word: \"").append(targetWord).append("\" (").append(targetLanguage.getName()).append(")\n");
        prompt.append("- Source language: ").append(sourceLanguage.getName()).append("\n");
        prompt.append("- Target language: ").append(targetLanguage.getName()).append("\n\n");
        
        prompt.append("🔥 CRITICAL CONSISTENCY REQUIREMENT 🔥\n");
        prompt.append("STEP 1: TRANSLATE THE TARGET WORD ONCE\n");
        prompt.append("First, translate \"").append(targetWord).append("\" from ").append(targetLanguage.getName())
              .append(" to ").append(sourceLanguage.getName()).append(".\n");
        prompt.append("This translation will be your SINGLE, CONSISTENT SOURCE WORD for ALL ").append(count).append(" flashcards.\n");
        prompt.append("❌ DO NOT use different translations, synonyms, or related words for different flashcards!\n");
        prompt.append("✅ USE THE EXACT SAME SOURCE WORD in every single flashcard!\n\n");
        
        prompt.append("COMPONENT RULES:\n");
        if (components.stream().anyMatch(c -> c instanceof SourceLanguageWord)) {
            prompt.append("- sourceWord: Use the EXACT SAME ").append(sourceLanguage.getName()).append(" translation of \"").append(targetWord).append("\" in ALL ").append(count).append(" flashcards\n");
            prompt.append("  ⚠️ This field must be IDENTICAL across all flashcards!\n");
        }
        if (components.stream().anyMatch(c -> c instanceof TargetLanguageWord)) {
            prompt.append("- targetWord: Use the exact word \"").append(targetWord).append("\" (").append(targetLanguage.getName()).append(") in ALL ").append(count).append(" flashcards\n");
            prompt.append("  ⚠️ This field must be IDENTICAL across all flashcards!\n");
        }
        if (components.stream().anyMatch(c -> c instanceof SourceLanguageSentence)) {
            prompt.append("- sourceSentence: Write ENTIRELY in ").append(sourceLanguage.getName())
                  .append(", using the SAME ").append(sourceLanguage.getName()).append(" translation of \"").append(targetWord).append("\" in every flashcard\n");
            prompt.append("  ❌ NEVER use \"").append(targetWord).append("\" in source sentences!\n");
            prompt.append("  ❌ NEVER use synonyms or different translations of \"").append(targetWord).append("\"!\n");
        }
        if (components.stream().anyMatch(c -> c instanceof TargetLanguageSentence)) {
            prompt.append("- targetSentence: Write ENTIRELY in ").append(targetLanguage.getName())
                  .append(", using the exact word \"").append(targetWord).append("\" in every flashcard\n");
        }
        prompt.append("\n");

        prompt.append("🎯 WORD CONSISTENCY EXAMPLES:\n");
        prompt.append("If translating \"").append(targetWord).append("\" to ").append(sourceLanguage.getName()).append(" gives you \"example_translation\":\n");
        prompt.append("✅ CORRECT: All flashcards use sourceWord: \"example_translation\"\n");
        prompt.append("❌ WRONG: Flashcard 1 uses \"example_translation\", Flashcard 2 uses \"synonym_word\"\n");
        prompt.append("❌ WRONG: Using related words instead of the direct translation\n\n");

        prompt.append("You are a language learning assistant.\n")
              .append("Generate ").append(count).append(" different flashcards as a JSON array.\n")
              .append("IMPORTANT: Each flashcard MUST use EXACTLY the word \"").append(targetWord)
              .append("\" in target language components - no synonyms or related words.\n")
              .append("CRITICAL: The sourceWord field must be IDENTICAL in all ").append(count).append(" flashcards!\n\n")
              .append(generateBasePrompt())
              .append(getComponentsDescription(components))
              .append(getLanguageSpecificInstructions(components, sourceLanguage, targetLanguage))
              .append("The structure for each flashcard in the array should be:\n")
              .append(getComponentsJsonStructure(components))
              .append(giveExampleSentence())
              .append("The word in the target language is \"").append(targetWord)
              .append("\" (").append(targetLanguage.getName()).append(") and needs to be translated to ")
              .append(sourceLanguage.getName()).append(".\n");

        prompt.append("FINAL REMINDERS:\n")
              .append("- Each flashcard should use different example sentences while maintaining accuracy\n")
              .append("- Every flashcard MUST use the exact word \"").append(targetWord).append("\" in target language components\n")
              .append("- The sourceWord field must be IDENTICAL across all ").append(count).append(" flashcards\n")
              .append("- Only the sentences should vary between flashcards, NOT the word translations\n")
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

    private String getComponentsDescription(List<FlashcardComponent> components) {
        return components.stream()
                .map(FlashcardComponent::getDescription)
                .collect(Collectors.joining(", ")) + "\n";
    }

    private String getLanguageSpecificInstructions(List<FlashcardComponent> components, Language sourceLanguage, Language targetLanguage) {
        StringBuilder instructions = new StringBuilder();
        instructions.append("\nLANGUAGE-SPECIFIC COMPONENT INSTRUCTIONS:\n");
        
        for (FlashcardComponent component : components) {
            if (component instanceof SourceLanguageWord) {
                instructions.append("- sourceWord: Must be in ").append(sourceLanguage.getName())
                           .append(" (translate the target word)\n");
            } else if (component instanceof TargetLanguageWord) {
                instructions.append("- targetWord: Must be in ").append(targetLanguage.getName())
                           .append(" (use exact word provided)\n");
            } else if (component instanceof SourceLanguageSentence) {
                instructions.append("- sourceSentence: Must be ENTIRELY in ").append(sourceLanguage.getName())
                           .append(", using the translated word, NEVER the target word\n");
            } else if (component instanceof TargetLanguageSentence) {
                instructions.append("- targetSentence: Must be ENTIRELY in ").append(targetLanguage.getName())
                           .append(", using the exact target word provided\n");
            }
        }
        instructions.append("\n");
        return instructions.toString();
    }

    private String getComponentsJsonStructure(List<FlashcardComponent> components) {
        return "{\n" +
               components.stream()
                       .map(FlashcardComponent::getJsonStructure)
                       .collect(Collectors.joining(",\n")) +
               "\n}\n";
    }

    private String getFormattingRules() {
        return "FORMATTING RULES:\n" +
               "1. First, translate the target word to the source language and use this translation consistently\n" +
               "2. Source and target words should be in lowercase\n" +
               "3. Example sentences must start with a capital letter and end with a period\n" +
               "4. Ensure proper sentence structure and punctuation in all example sentences\n" +
               "5. 🚨 ABSOLUTE RULE: Source language sentences must NEVER contain the target word\n" +
               "6. 🚨 ABSOLUTE RULE: Target language sentences must NEVER contain the source word translation\n" +
               "7. Double-check every sentence to ensure correct language usage\n" +
               "8. Each language component must be written entirely in its designated language\n";
    }

    private String giveExampleSentence() {
        return "EXAMPLE TO CLARIFY CONSISTENCY:\n" +
               "If source language is English and target language is Spanish:\n" +
               "- Target word: \"libro\" (Spanish) - SAME in all flashcards\n" +
               "- Source word: \"book\" (English translation) - SAME in all flashcards\n\n" +
               "FLASHCARD 1:\n" +
               "- sourceWord: \"book\" ✅\n" +
               "- targetWord: \"libro\" ✅\n" +
               "- sourceSentence: \"The book is on the table\" ✅\n" +
               "- targetSentence: \"El libro está en la mesa\" ✅\n\n" +
               "FLASHCARD 2:\n" +
               "- sourceWord: \"book\" ✅ (SAME as flashcard 1)\n" +
               "- targetWord: \"libro\" ✅ (SAME as flashcard 1)\n" +
               "- sourceSentence: \"I read a good book yesterday\" ✅ (DIFFERENT sentence, SAME word)\n" +
               "- targetSentence: \"Leí un buen libro ayer\" ✅ (DIFFERENT sentence, SAME word)\n\n" +
               "❌ WRONG EXAMPLES:\n" +
               "- sourceWord: \"novel\" (different word - should be \"book\")\n" +
               "- sourceWord: \"publication\" (synonym - should be \"book\")\n" +
               "- sourceSentence: \"The libro is on the table\" (mixing languages)\n" +
               "- targetSentence: \"El book está en la mesa\" (mixing languages)\n\n";
    }
}
