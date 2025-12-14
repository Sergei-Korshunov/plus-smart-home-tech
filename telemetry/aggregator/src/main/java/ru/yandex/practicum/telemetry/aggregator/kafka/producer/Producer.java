package ru.yandex.practicum.telemetry.aggregator.kafka.producer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@EnableConfigurationProperties({ProducerProperty.class})
@Configuration
public class Producer {
    private final ProducerProperty producerProperty;

    @Autowired
    public Producer(ProducerProperty producerProperty) {
        this.producerProperty = producerProperty;
    }

    @Bean
    public KafkaProducer<String, SpecificRecordBase> getProducerInstance() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, producerProperty.getBootstrapServers());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerProperty.getKeySerializerClass());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerProperty.getValueSerializerClass());

        return new KafkaProducer<>(properties);
    }
}