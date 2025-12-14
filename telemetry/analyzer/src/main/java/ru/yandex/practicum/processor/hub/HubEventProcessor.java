package ru.yandex.practicum.processor.hub;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.hub.HubEventService;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {
    private final KafkaConsumer<String, HubEventAvro> consumer;
    private final Map<String, HubEventService> hubEventServices;

    @Value("${analyzer.topic.hub-event-topic}")
    private String hubEventTopic;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    @Autowired
    public HubEventProcessor(KafkaConsumer<String, HubEventAvro> consumer, Set<HubEventService> hubEventServices) {
        this.consumer = consumer;
        this.hubEventServices = hubEventServices.stream().collect(Collectors.toMap(
                HubEventService::getType,
                Function.identity()
        ));
    }

    @Override
    public void run() {
        start();
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(List.of(hubEventTopic));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(500));

                int count = 0;
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    log.info("Обрабатываем сообщение: топик = {}, партиция = {}, смещение = {}, значение: {}",
                            record.topic(), record.partition(), record.offset(), record.value());

                    HubEventAvro hubEventAvro = record.value();
                    String payloadName = hubEventAvro.getPayload().getClass().getSimpleName();
                    log.info("Получено сообщение от хаба {}", payloadName);

                    if (hubEventServices.containsKey(payloadName)) {
                        hubEventServices.get(payloadName).perform(hubEventAvro);
                    } else {
                        log.warn("Обработчик для хаба {} не найден", payloadName);
                        throw new IllegalArgumentException(String.format("Обработчик для хаба %s не найден", payloadName));
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
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    public void stop() {
        Optional.ofNullable(consumer).ifPresent(KafkaConsumer::wakeup);
    }

    // только для отладки
    private void printHashMapHubEventServices(Map<String, HubEventService> hubEventServices) {
        log.debug("Данные внутри карты, которая включает в себя ключ-тип события, значение-событие:");
        for (Map.Entry<String, HubEventService> entries : hubEventServices.entrySet()) {
            log.debug("{} : {}", entries.getKey(), entries.getValue());
        }
    }

    private void fixOffsets(ConsumerRecord<String, HubEventAvro> record, int count, KafkaConsumer<String, HubEventAvro> consumer) {
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