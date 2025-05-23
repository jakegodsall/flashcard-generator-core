package com.jakegodsall.services.json.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jakegodsall.models.enums.FlashcardType;
import com.jakegodsall.models.flashcards.Flashcard;
import com.jakegodsall.models.flashcards.SentenceFlashcard;
import com.jakegodsall.models.flashcards.WordFlashcard;
import lombok.RequiredArgsConstructor;
import com.jakegodsall.services.json.JsonParseService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementation of JsonParseService for GPT-specific JSON parsing.
 */
@RequiredArgsConstructor
public class JsonParseServiceGPTImpl implements JsonParseService {
    private final ObjectMapper objectMapper;

    @Override
    public List<String> parseModels(String json) throws IOException {
        JsonNode rootNode = objectMapper.readTree(json);
        List<String> models = new ArrayList<>();

        JsonNode dataNode = rootNode.path("data");
        if (dataNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) dataNode;
            Iterator<JsonNode> elements = arrayNode.elements();
            while (elements.hasNext()) {
                String modelName = elements.next().path("id").asText();
                if (!modelName.isEmpty()) {
                    models.add(modelName);
                }
            }
        } else {
            throw new NoSuchElementException("Missing 'data' field in the JSON response");
        }
        return models;
    }

    @Override
    public Flashcard parseFlashcard(String responseBody, FlashcardType flashcardType) {
        try {
            String content = parseContentFromResponse(responseBody);

            // Parse the JSON into a JsonNode tree
            JsonNode rootNode = objectMapper.readTree(content);

            // Delegate to specific parsing logic based on FlashcardType
            return switch (flashcardType) {
                case WORD -> parseWordFlashcardFromJson(rootNode);
                case SENTENCE -> parseSentenceFlashcardFromJson(rootNode);
                default -> throw new IllegalArgumentException("Unsupported FlashcardType: " + flashcardType);
            };
        } catch (JsonProcessingException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    @Override
    public List<Flashcard> parseMultipleFlashcards(String responseBody, FlashcardType flashcardType) {
        try {
            String content = parseContentFromResponse(responseBody);
            JsonNode rootNode = objectMapper.readTree(content);

            List<Flashcard> flashcards = new ArrayList<>();

            if (!rootNode.isArray()) {
                throw new NoSuchElementException("Expected an array of flashcards in the response");
            }

            for (JsonNode flashcardNode : rootNode) {
                Flashcard flashcard = switch (flashcardType) {
                    case WORD -> parseWordFlashcardFromJson(flashcardNode);
                    case SENTENCE -> parseSentenceFlashcardFromJson(flashcardNode);
                    default -> throw new IllegalArgumentException("Unsupported FlashcardType: " + flashcardType);
                };
                flashcards.add(flashcard);
            }

            return flashcards;
        } catch (JsonProcessingException ex) {
            System.err.println(ex.getMessage());
            return List.of();
        }
    }

    /**
     * Parses a WordFlashcard from the given JsonNode.
     */
    private WordFlashcard parseWordFlashcardFromJson(JsonNode rootNode) {
        JsonNode sourceWordNode = getJsonNode(rootNode, "sourceWord");
        JsonNode targetWordNode = getJsonNode(rootNode, "targetWord");
        JsonNode targetSentenceNode = getJsonNode(rootNode, "targetSentence");

        return new WordFlashcard(
                sourceWordNode.asText(),
                targetWordNode.asText(),
                targetSentenceNode.asText()
        );
    }

    /**
     * Parses a SentenceFlashcard from the given JsonNode.
     */
    private SentenceFlashcard parseSentenceFlashcardFromJson(JsonNode rootNode) {
        JsonNode sourceSentenceNode = getJsonNode(rootNode, "sourceSentence");
        JsonNode targetSentenceNode = getJsonNode(rootNode, "targetSentence");

        return new SentenceFlashcard(
                sourceSentenceNode.asText(),
                targetSentenceNode.asText()
        );
    }

    /**
     * Retrieves a JsonNode from the root and throws an exception if the field is missing.
     */
    private JsonNode getJsonNode(JsonNode rootNode, String fieldName) {
        JsonNode node = rootNode.path(fieldName);
        if (node.isMissingNode()) {
            throw new NoSuchElementException("Missing '" + fieldName + "' field in the JSON response");
        }
        return node;
    }

    /**
     * Extracts the "content" field from the API response JSON.
     * Throws an exception if any required field is missing.
     */
    private String parseContentFromResponse(String responseBody) throws JsonProcessingException {
        // Parse the JSON into a JsonNode tree
        JsonNode rootNode = objectMapper.readTree(responseBody);

        // Navigate the JSON tree to extract the content
        JsonNode choicesNode = rootNode.path("choices");
        if (choicesNode.isMissingNode() || !choicesNode.isArray() || choicesNode.isEmpty())
            throw new NoSuchElementException("Missing 'choices' field in the JSON response");
        JsonNode firstChoiceNode = choicesNode.get(0);
        if (firstChoiceNode.isMissingNode())
            throw new NoSuchElementException("'choices' array is empty");
        JsonNode messageNode = firstChoiceNode.path("message");
        if (messageNode.isMissingNode())
            throw new NoSuchElementException("Missing 'message' field in the JSON response");
        JsonNode contentNode = messageNode.path("content");
        if (contentNode.isMissingNode())
            throw new NoSuchElementException("Missing 'content' field in the JSON response");

        // Return the response
        String content = "";
        if (contentNode.isObject() || contentNode.isArray())
            content = contentNode.toString();
        if (contentNode.isValueNode())
            content = contentNode.asText();
        return content;
    }
}
