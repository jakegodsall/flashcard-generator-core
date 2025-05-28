package com.jakegodsall.models.flashcards;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Map;

/**
 * Custom Jackson serializer for the Flashcard class.
 * This serializer flattens the component values into the root object.
 */
public class FlashcardSerializer extends StdSerializer<Flashcard> {
    public FlashcardSerializer() {
        super(Flashcard.class);
    }

    @Override
    public void serialize(Flashcard flashcard, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        for (Map.Entry<String, String> entry : flashcard.getComponentValues().entrySet()) {
            gen.writeStringField(entry.getKey(), entry.getValue());
        }
        gen.writeEndObject();
    }
} 