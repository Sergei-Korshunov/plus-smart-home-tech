package ru.yandex.practicum.processor.snapshot.property;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@EnableConfigurationProperties({SnapshotConsumerProperty.class})
@Configuration
public class SnapshotConsumer {
    private final SnapshotConsumerProperty snapshotConsumerProperty;

    @Autowired
    public SnapshotConsumer(SnapshotConsumerProperty snapshotConsumerProperty) {
        this.snapshotConsumerProperty = snapshotConsumerProperty;
    }

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> getSnapshotConsumerInstance() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, snapshotConsumerProperty.getBootstrapServers());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, snapshotConsumerProperty.getKeyDeserializer());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, snapshotConsumerProperty.getValueDeserializer());

        properties.put(ConsumerConfig.GROUP_ID_CONFIG, snapshotConsumerProperty.getGroupId());
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, snapshotConsumerProperty.getClientId());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, snapshotConsumerProperty.getAutoOffsetReset());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, snapshotConsumerProperty.getEnableAutoCommit());

        return new KafkaConsumer<>(properties);
    }
}