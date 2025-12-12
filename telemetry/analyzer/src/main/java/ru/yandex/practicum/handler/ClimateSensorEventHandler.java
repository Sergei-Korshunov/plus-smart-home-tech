package ru.yandex.practicum.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.ConditionType;
import ru.yandex.practicum.model.SensorEventType;

@Slf4j
@Component
public class ClimateSensorEventHandler implements BaseSensorEventHandler {
    @Override
    public String getType() {
        return ClimateSensorAvro.class.getName();
    }

    @Override
    public Integer getSensorValue(ConditionType type, SensorStateAvro sensorStateAvro) {
        ClimateSensorAvro climateSensorAvro = (ClimateSensorAvro) sensorStateAvro.getData();
        return switch (type) {
            case ConditionType.TEMPERATURE -> climateSensorAvro.getTemperatureC();
            case ConditionType.CO2LEVEL -> climateSensorAvro.getCo2Level();
            case ConditionType.HUMIDITY -> climateSensorAvro.getHumidity();
            default -> null;
        };
    }
}