package ru.yandex.practicum.telemetry.aggregator.kafka.consumer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka.consumer")
public class ConsumerProperty {
    private String bootstrapServers;
    private String keyDeserializerClass;
    private String valueDeserializerClass;
    private String groupId;
    private String clientId;
    private String enableAutoCommit;
}