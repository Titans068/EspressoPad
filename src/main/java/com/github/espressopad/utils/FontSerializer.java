package com.github.espressopad.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.awt.Font;
import java.io.IOException;

public class FontSerializer extends JsonSerializer<Font> {
    @Override
    public void serialize(Font value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(String.format("%s,%d", value.getFontName(), value.getSize()));
    }
}
