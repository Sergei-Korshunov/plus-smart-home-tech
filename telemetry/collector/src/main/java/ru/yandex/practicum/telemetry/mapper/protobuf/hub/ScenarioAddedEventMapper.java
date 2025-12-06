package ru.yandex.practicum.telemetry.mapper.protobuf.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.mapper.protobuf.TimestampMapper;

import java.util.List;

@Component
public class ScenarioAddedEventMapper implements HubEventMapper {

    @Override
    public SpecificRecordBase mapSimpleObjectToProto(HubEventProto event) {
        ScenarioAddedEventProto scenarioAddedEventProto = event.getScenarioAdded();

        List<ScenarioConditionAvro> conditions = scenarioAddedEventProto.getConditionList().stream()
                .map(c -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(c.getSensorId())
                        .setType(ConditionTypeAvro.valueOf(c.getType().name()))
                        .setOperation(ConditionOperationAvro.valueOf(c.getOperation().name()))
                        .setValue(resolveScenarioConditionValue(c))
                        .build())
                .toList();

        List<DeviceActionAvro> actions = scenarioAddedEventProto.getActionList().stream()
                .map(a -> DeviceActionAvro.newBuilder()
                        .setSensorId(a.getSensorId())
                        .setType(ActionTypeAvro.valueOf(a.getType().name()))
                        .setValue(a.hasValue() ? a.getValue() : null)
                        .build())
                .toList();

        ScenarioAddedEventAvro payload = ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEventProto.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();

        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(TimestampMapper.toInstant(event.hasTimestamp() ? event.getTimestamp() : null))
                .setPayload(payload)
                .build();
    }

    private Object resolveScenarioConditionValue(ScenarioConditionProto condition) {
        return switch (condition.getValueCase()) {
            case BOOL_VALUE -> condition.getBoolValue();
            case INT_VALUE -> condition.getIntValue();
            default -> null;
        };
    }

    @Override
    public HubEventProto.PayloadCase getType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }
}