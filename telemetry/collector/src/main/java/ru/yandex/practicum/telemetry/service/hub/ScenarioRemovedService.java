package ru.yandex.practicum.telemetry.service.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.kafka.KafkaClient;
import ru.yandex.practicum.telemetry.mapper.protobuf.hub.HubEventMapper;

import java.util.Set;

@Component
public class ScenarioRemovedService extends HubEventService {

    public ScenarioRemovedService(KafkaClient kafkaClient, Set<HubEventMapper> hubEventMappers) {
        super(kafkaClient, hubEventMappers);
    }

    @Override
    public void push(HubEventProto hubEventProto) {
        HubEventMapper hubEventMapper = getMapper(hubEventProto);

        if (hubEventMapper != null) {
            KafkaClient kafkaClient = getKafkaClient();
            kafkaClient.sendData(kafkaClient.getKafkaProperty().getNameTopicHub(),
                    null,
                    hubEventProto.getTimestamp().getSeconds(), // ???
                    hubEventProto.getHubId(),
                    hubEventMapper.mapSimpleObjectToProto(hubEventProto));
        }
    }

    @Override
    public HubEventProto.PayloadCase getType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }
}