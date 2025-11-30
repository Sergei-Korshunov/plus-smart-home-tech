package ru.yandex.practicum.telemetry.mapper.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.model.sensor.SensorEventType;
import ru.yandex.practicum.telemetry.model.sensor.SwitchSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
public class SwitchSensorEventMapper implements SensorEventMapper {

    @Override
    public SpecificRecordBase mapSimpleObjectToAvro(SensorEvent sensorEvent) {
        SwitchSensorEvent sw = (SwitchSensorEvent) sensorEvent;

        SwitchSensorAvro payload = SwitchSensorAvro.newBuilder()
                .setState(sw.getState())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(sw.getId())
                .setHubId(sw.getHubId())
                .setTimestamp(sw.getTimestamp())
                .setPayload(payload)
                .build();
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}