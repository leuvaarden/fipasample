package com.github.leuvaarden.fipasample.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class SerializationUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private SerializationUtils() {
    }

    public static String serialize(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public static <T> T deserialize(String string, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(string, clazz);
    }

    public static <T> T deserialize(String string, TypeReference<T> typ) throws JsonProcessingException {
        return objectMapper.readValue(string, typ);
    }
}
