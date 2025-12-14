package ru.yandex.practicum.model;

import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

public enum ConditionType {
    MOTION,
    LUMINOSITY,
    SWITCH,
    TEMPERATURE,
    CO2LEVEL,
    HUMIDITY;

    public static ConditionType fromAvro(ConditionTypeAvro conditionTypeAvro) {
        return ConditionType.valueOf(conditionTypeAvro.name());
    }
}