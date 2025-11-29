package ru.yandex.practicum.telemetry.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka.property")
public class KafkaProperty {
    private String bootstrapServers;
    private String keySerializerClass;
    private String valueSerializerClass;
}