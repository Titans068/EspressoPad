package com.github.espressopad.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.awt.Font;
import java.io.IOException;

public class FontDeserializer extends JsonDeserializer<Font> {
    @Override
    public Font deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String[] value = p.getValueAsString().split(",");
        return new Font(value[0], Font.PLAIN, Integer.parseInt(value[1]));
    }
}
