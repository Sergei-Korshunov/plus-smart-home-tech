package ru.yandex.practicum.telemetry.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.kafka.KafkaClient;
import ru.yandex.practicum.telemetry.mapper.hub.HubEventMapper;
import ru.yandex.practicum.telemetry.mapper.sensor.SensorEventMapper;
import ru.yandex.practicum.telemetry.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.model.hub.HubEventType;
import ru.yandex.practicum.telemetry.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.model.sensor.SensorEventType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CollectorServiceImpl implements CollectorService {
    private final KafkaClient kafkaClient;
    private final Map<SensorEventType, SensorEventMapper> sensorEventMappers;
    private final Map<HubEventType, HubEventMapper> hubEventMappers;

    @Autowired
    public CollectorServiceImpl(KafkaClient kafkaClient, List<SensorEventMapper> sensorEventMapperList,
                                List<HubEventMapper> hubEventMapperList) {
        this.kafkaClient = kafkaClient;

        this.sensorEventMappers = sensorEventMapperList.stream()
                .collect(Collectors.toMap(SensorEventMapper::getType, Function.identity()));
        this.hubEventMappers = hubEventMapperList.stream()
                .collect(Collectors.toMap(HubEventMapper::getType, Function.identity()));
    }

    @Override
    public void hubEvent(HubEvent hubEvent) {
        log.info("Вызов метода hubEvent()");
        log.info("2. Обработка события с типом: {}", hubEvent.getType());
        final String topicName = "telemetry.hubs.v1";

        HubEventMapper hubEventMapper = null;
        if (hubEventMappers.containsKey(hubEvent.getType())) {
            hubEventMapper = hubEventMappers.get(hubEvent.getType());

            kafkaClient.sendData(topicName,
                    null,
                    hubEvent.getTimestamp().toEpochMilli(),
                    hubEvent.getHubId(),
                    hubEventMapper.mapSimpleObjectToAvro(hubEvent));
        } else {
            log.warn("Неподдерживаемый тип события {}", hubEvent.getType());
        }
    }

    @Override
    public void sensorEvent(SensorEvent sensorEvent) {
        log.info("Вызов метода sensorEvent()");
        log.info("2. Обработка события с типом: {}", sensorEvent.getType());
        final String topicName = "telemetry.sensors.v1";

        SensorEventMapper sensorEventMapper = null;
        if (sensorEventMappers.containsKey(sensorEvent.getType())) {
            sensorEventMapper = sensorEventMappers.get(sensorEvent.getType());

            kafkaClient.sendData(topicName,
                    null,
                    sensorEvent.getTimestamp().toEpochMilli(),
                    sensorEvent.getHubId(),
                    sensorEventMapper.mapSimpleObjectToAvro(sensorEvent));
        } else {
            log.warn("Неподдерживаемый тип события {}", sensorEvent.getType());
        }
    }
}