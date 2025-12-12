package ru.yandex.practicum.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.ConditionType;

@Slf4j
@Component
public class LightSensorEventHandler implements BaseSensorEventHandler {
    @Override
    public String getType() {
        return LightSensorAvro.class.getName();
    }

    @Override
    public Integer getSensorValue(ConditionType type, SensorStateAvro sensorStateAvro) {
        LightSensorAvro lightSensorAvro = (LightSensorAvro) sensorStateAvro.getData();
        return switch (type) {
            case ConditionType.LUMINOSITY -> lightSensorAvro.getLuminosity();
            default -> null;
        };
    }
}