package ru.yandex.practicum.handler;

import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.ConditionType;

public interface BaseSensorEventHandler {
    String getType();

    Integer getSensorValue(ConditionType type, SensorStateAvro sensor);
}
