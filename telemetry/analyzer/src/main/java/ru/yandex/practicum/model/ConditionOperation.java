package ru.yandex.practicum.model;

import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;

public enum ConditionOperation {
    EQUALS,
    GREATER_THAN,
    LOWER_THAN;

    public static ConditionOperation fromAvro(ConditionOperationAvro operationAvro) {
        return ConditionOperation.valueOf(operationAvro.name());
    }
}