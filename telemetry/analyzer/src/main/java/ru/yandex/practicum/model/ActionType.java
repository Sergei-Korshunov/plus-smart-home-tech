package ru.yandex.practicum.model;

import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

public enum ActionType {
    ACTIVATE,
    DEACTIVATE,
    INVERSE,
    SET_VALUE;

    public static ActionType fromAvro(ActionTypeAvro actionTypeAvro) {
        return ActionType.valueOf(actionTypeAvro.name());
    }

    public static ActionTypeProto toProto(ActionType type) {
        return ActionTypeProto.valueOf(type.name());
    }
}