package ru.yandex.practicum.telemetry.aggregator.kafka.producer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka.producer")
public class ProducerProperty {
    private String bootstrapServers;
    private String keySerializerClass;
    private String valueSerializerClass;
}