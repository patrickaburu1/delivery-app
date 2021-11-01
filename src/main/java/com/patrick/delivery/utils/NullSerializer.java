package com.patrick.delivery.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author patrick on 6/8/20
 * @project sprintel-delivery
 */
public class NullSerializer extends JsonSerializer<Object> {
    // Generate the preferred JSON value
    @Override
    public void serialize(Object t, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
        jg.writeString("");
    }
}

