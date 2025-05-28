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
        
        prompt.append("STEP 1: TRANSLATE THE TARGET WORD\n");
        prompt.append("First, translate \"").append(targetWord).append("\" from ").append(targetLanguage.getName())
              .append(" to ").append(sourceLanguage.getName()).append(".\n");
        prompt.append("This translation will be your SOURCE WORD for all flashcards.\n\n");
        
        prompt.append("COMPONENT RULES:\n");
        if (components.stream().anyMatch(c -> c instanceof SourceLanguageWord)) {
            prompt.append("- sourceWord: Use ONLY the ").append(sourceLanguage.getName()).append(" translation of \"").append(targetWord).append("\"\n");
        }
        if (components.stream().anyMatch(c -> c instanceof TargetLanguageWord)) {
            prompt.append("- targetWord: Use ONLY the exact word \"").append(targetWord).append("\" (").append(targetLanguage.getName()).append(")\n");
        }
        if (components.stream().anyMatch(c -> c instanceof SourceLanguageSentence)) {
            prompt.append("- sourceSentence: Write ENTIRELY in ").append(sourceLanguage.getName())
                  .append(", using the ").append(sourceLanguage.getName()).append(" translation of \"").append(targetWord).append("\"\n");
            prompt.append("  ❌ NEVER use \"").append(targetWord).append("\" in source sentences!\n");
        }
        if (components.stream().anyMatch(c -> c instanceof TargetLanguageSentence)) {
            prompt.append("- targetSentence: Write ENTIRELY in ").append(targetLanguage.getName())
                  .append(", using the exact word \"").append(targetWord).append("\"\n");
        }
        prompt.append("\n");

        prompt.append("You are a language learning assistant.\n")
              .append("Generate ").append(count).append(" different flashcards as a JSON array.\n")
              .append("IMPORTANT: Each flashcard MUST use EXACTLY the word \"").append(targetWord)
              .append("\" in target language components - no synonyms or related words.\n\n")
              .append(generateBasePrompt())
              .append(getComponentsDescription(components))
              .append(getLanguageSpecificInstructions(components, sourceLanguage, targetLanguage))
              .append("The structure for each flashcard in the array should be:\n")
              .append(getComponentsJsonStructure(components))
              .append(giveExampleSentence())
              .append("The word in the target language is \"").append(targetWord)
              .append("\" (").append(targetLanguage.getName()).append(") and needs to be translated to ")
              .append(sourceLanguage.getName()).append(".\n");

        prompt.append("Each flashcard should use different example sentences while maintaining accuracy.\n")
              .append("Remember: Every flashcard MUST use the exact word \"").append(targetWord)
              .append("\" in target language components - not synonyms, not related words.\n")
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
        return "EXAMPLE TO CLARIFY:\n" +
               "If source language is English and target language is Spanish:\n" +
               "- Target word: \"libro\" (Spanish)\n" +
               "- Source word: \"book\" (English translation)\n" +
               "- sourceSentence: \"The book is on the table\" (English, using \"book\")\n" +
               "- targetSentence: \"El libro está en la mesa\" (Spanish, using \"libro\")\n" +
               "❌ WRONG: sourceSentence: \"The libro is on the table\" (mixing languages)\n" +
               "❌ WRONG: targetSentence: \"El book está en la mesa\" (mixing languages)\n\n";
    }
}
