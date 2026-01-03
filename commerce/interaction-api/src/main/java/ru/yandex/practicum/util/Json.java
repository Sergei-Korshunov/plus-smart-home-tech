package ru.yandex.practicum.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Json {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    static  {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private Json() {}

    public static String simpleObjectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации простого объекта в json формат: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}