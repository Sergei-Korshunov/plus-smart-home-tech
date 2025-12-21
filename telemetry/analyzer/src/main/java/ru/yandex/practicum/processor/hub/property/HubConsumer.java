package ru.yandex.practicum.processor.hub.property;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.Properties;

@EnableConfigurationProperties({HubConsumerProperty.class})
@Configuration
public class HubConsumer {
    private final HubConsumerProperty hubConsumerProperty;

    @Autowired
    public HubConsumer(HubConsumerProperty hubConsumerProperty) {
        this.hubConsumerProperty = hubConsumerProperty;
    }

    @Bean
    public KafkaConsumer<String, HubEventAvro> getHubEventConsumerInstance() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hubConsumerProperty.getBootstrapServers());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, hubConsumerProperty.getKeyDeserializer());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, hubConsumerProperty.getValueDeserializer());

        properties.put(ConsumerConfig.GROUP_ID_CONFIG, hubConsumerProperty.getGroupId());
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, hubConsumerProperty.getClientId());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, hubConsumerProperty.getAutoOffsetReset());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, hubConsumerProperty.getEnableAutoCommit());

        return new KafkaConsumer<>(properties);
    }
}