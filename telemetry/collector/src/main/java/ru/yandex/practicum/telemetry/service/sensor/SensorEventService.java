package ru.yandex.practicum.telemetry.service.sensor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.kafka.KafkaClient;
import ru.yandex.practicum.telemetry.mapper.protobuf.sensor.SensorEventMapper;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class SensorEventService {
    @Getter
    private KafkaClient kafkaClient;
    private final Map<SensorEventProto.PayloadCase, SensorEventMapper> sensorEventMappers;

    @Autowired
    public SensorEventService(KafkaClient kafkaClient, Set<SensorEventMapper> sensorEventMappers) {
        this.kafkaClient = kafkaClient;
        this.sensorEventMappers = sensorEventMappers.stream()
                .collect(Collectors.toMap(SensorEventMapper::getType, Function.identity()));
    }

    public abstract void push(SensorEventProto sensorEventProto);

    public abstract SensorEventProto.PayloadCase getType();

    public SensorEventMapper getMapper(SensorEventProto sensorEventProto) {
        log.info("Поиск mapper для события {}", sensorEventProto.getPayloadCase());
        if (sensorEventMappers.containsKey(sensorEventProto.getPayloadCase())) {
            log.info("Mapper для события {} найден", sensorEventProto.getPayloadCase().name());
            return sensorEventMappers.get(sensorEventProto.getPayloadCase());
        } else {
            log.warn("Неподдерживаемый тип события {}", sensorEventProto.getPayloadCase());
        }

        log.warn("Mapper для события {} равен null", sensorEventProto.getPayloadCase().name());
        return null;
    }
}