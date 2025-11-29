package ru.yandex.practicum.telemetry.kafka;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@EnableConfigurationProperties({KafkaProperty.class})
@Component
public class KafkaClient {
    private final KafkaProperty kafkaProperty;
    private final Producer<String, SpecificRecordBase> producer;

    @Autowired
    public KafkaClient(KafkaProperty kafkaProperty) {
        this.kafkaProperty = kafkaProperty;
        producer = producerInstance();
    }

    protected Producer<String, SpecificRecordBase> producerInstance() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperty.getBootstrapServers());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProperty.getKeySerializerClass());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProperty.getValueSerializerClass());
        return new KafkaProducer<>(properties);
    }

    public void sendData(String topicName, Integer partition, Long timestamp, String key, SpecificRecordBase value) {
        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(
                topicName,
                partition,
                timestamp,
                key,
                value
        );

        producer.send(producerRecord);
        producer.flush();
        log.info("4. Отправка данных в Kafka. Данные успешно отправлены в топик {}", topicName);
    }

    @PreDestroy
    public void close() {
        producer.flush();
        producer.close();
        log.info("Закрытие Producer");
    }
}