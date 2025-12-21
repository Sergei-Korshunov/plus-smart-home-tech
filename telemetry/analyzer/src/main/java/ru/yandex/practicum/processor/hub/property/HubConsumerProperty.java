package ru.yandex.practicum.processor.hub.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka.consumer.hub")
public class HubConsumerProperty {
    private String bootstrapServers;
    private String keyDeserializer;
    private String valueDeserializer;
    private String groupId;
    private String clientId;
    private String autoOffsetReset;
    private String enableAutoCommit;
}