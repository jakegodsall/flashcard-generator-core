package com.jakegodsall.services.json.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jakegodsall.models.flashcards.Flashcard;
import com.jakegodsall.models.flashcards.components.FlashcardComponent;
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
    public Flashcard parseFlashcard(String responseBody, List<FlashcardComponent> components) {
        try {
            String content = parseContentFromResponse(responseBody);
            JsonNode rootNode = objectMapper.readTree(content);
            return parseFlashcardFromJson(rootNode, components);
        } catch (JsonProcessingException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    @Override
    public List<Flashcard> parseMultipleFlashcards(String responseBody, List<FlashcardComponent> components) {
        try {
            String content = parseContentFromResponse(responseBody);
            JsonNode rootNode = objectMapper.readTree(content);

            List<Flashcard> flashcards = new ArrayList<>();

            if (!rootNode.isArray()) {
                throw new NoSuchElementException("Expected an array of flashcards in the response");
            }

            for (JsonNode flashcardNode : rootNode) {
                flashcards.add(parseFlashcardFromJson(flashcardNode, components));
            }

            return flashcards;
        } catch (JsonProcessingException ex) {
            System.err.println(ex.getMessage());
            return List.of();
        }
    }

    /**
     * Parses a flashcard from the given JsonNode based on the provided components.
     */
    private Flashcard parseFlashcardFromJson(JsonNode rootNode, List<FlashcardComponent> components) {
        // Create a map of component CSV column names to their values
        var componentValues = new java.util.HashMap<String, String>();
        
        // Extract values for each component
        for (FlashcardComponent component : components) {
            String columnName = component.getCsvColumnName();
            JsonNode node = getJsonNode(rootNode, columnName);
            componentValues.put(columnName, node.asText());
        }

        // Create a new Flashcard with the extracted values
        return new Flashcard(componentValues);
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
