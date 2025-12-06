package ru.yandex.practicum.telemetry.service.hub;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.kafka.KafkaClient;
import ru.yandex.practicum.telemetry.mapper.protobuf.hub.HubEventMapper;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class HubEventService {
    @Getter
    private KafkaClient kafkaClient;
    private final Map<HubEventProto.PayloadCase, HubEventMapper> hubEventMappers;

    @Autowired
    public HubEventService(KafkaClient kafkaClient, Set<HubEventMapper> hubEventMappers) {
        this.kafkaClient = kafkaClient;
        this.hubEventMappers = hubEventMappers.stream()
                .collect(Collectors.toMap(HubEventMapper::getType, Function.identity()));
    }

    public abstract void push(HubEventProto hubEventProto);

    public abstract HubEventProto.PayloadCase getType();

    public HubEventMapper getMapper(HubEventProto hubEventProto) {
        log.info("Поиск mapper для события {}", hubEventProto.getPayloadCase());
        if (hubEventMappers.containsKey(hubEventProto.getPayloadCase())) {
            log.warn("Mapper для события {} найден", hubEventProto.getPayloadCase().name());
            return hubEventMappers.get(hubEventProto.getPayloadCase());
        } else {
            log.warn("Неподдерживаемый тип события {}", hubEventProto.getPayloadCase());
        }

        log.warn("Mapper для события {} равен null", hubEventProto.getPayloadCase().name());
        return null;
    }
}