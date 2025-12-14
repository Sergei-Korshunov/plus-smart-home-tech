package ru.yandex.practicum.processor.snapshot.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka.consumer.snapshots")
public class SnapshotConsumerProperty {
    //@Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;
    private String keyDeserializer;
    private String valueDeserializer;
    private String groupId;
    private String clientId;
    private String autoOffsetReset;
    private String enableAutoCommit;
}