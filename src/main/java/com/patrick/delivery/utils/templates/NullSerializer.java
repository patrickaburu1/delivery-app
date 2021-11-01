package com.patrick.delivery.utils.templates;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * This class overwrites Jackson JSON NullSerializer to output an empty String
 * for Bean fields with null values
 * @author SamKyalo
 */

public class NullSerializer  extends JsonSerializer<Object> {
    // Generate the preferred JSON value
    @Override
    public void serialize(Object t, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
        jg.writeString("");
    }
}
