package ru.yandex.practicum.telemetry.aggregator;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.service.SnapshotService;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
public class AggregationStarter {
    private final KafkaConsumer<String, SpecificRecordBase> consumer;

    private final KafkaProducer<String, SpecificRecordBase> producer;

    private final SnapshotService snapshotService;

    @Value("${kafka.topic.telemetry-sensors}")
    private String sensorsTopic;

    @Value("${kafka.topic.telemetry-snapshots}")
    private String snapshotsTopic;

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    @Autowired
    public AggregationStarter(KafkaConsumer<String, SpecificRecordBase> consumer,
                              KafkaProducer<String, SpecificRecordBase> producer, SnapshotService snapshotService) {
        this.consumer = consumer;
        this.producer = producer;
        this.snapshotService = snapshotService;
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(List.of(sensorsTopic));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(500));

                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    log.info("Обрабатываем сообщение: топик = {}, партиция = {}, смещение = {}, значение: {}",
                            record.topic(), record.partition(), record.offset(), record.value());

                    SensorEventAvro event = (SensorEventAvro) record.value();
                    Optional<SensorsSnapshotAvro> snapshot = snapshotService.updateState(event);

                    if (snapshot.isPresent()) {
                        producer.send(new ProducerRecord<>(
                                snapshotsTopic,
                                null,
                                event.getTimestamp().toEpochMilli(),
                                event.getHubId(),
                                snapshot.get()
                        ));
                        log.info("Снапшот обновлен: {}", snapshot);
                    } else {
                        log.info("Снапшот не обновлен");
                    }
                    // фиксируем оффсеты обработанных записей, если нужно
                    fixOffsets(record, count, consumer);
                    count++;
                }
                // фиксируем максимальный оффсет обработанных записей
                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }

    private void fixOffsets(ConsumerRecord<String, SpecificRecordBase> record, int count, KafkaConsumer<String, SpecificRecordBase> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if(exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }
}