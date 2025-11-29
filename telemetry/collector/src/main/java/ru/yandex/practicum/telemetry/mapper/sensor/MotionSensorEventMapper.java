package ru.yandex.practicum.telemetry.mapper.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.telemetry.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.model.sensor.SensorEventType;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Component
public class MotionSensorEventMapper implements SensorEventMapper {

    @Override
    public SpecificRecordBase mapSimpleObjectToAvro(SensorEvent sensorEvent) {
        MotionSensorEvent motion = (MotionSensorEvent) sensorEvent;

        MotionSensorAvro payload = MotionSensorAvro.newBuilder()
                .setLinkQuality(motion.getLinkQuality())
                .setMotion(motion.getMotion())
                .setVoltage(motion.getVoltage())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(motion.getId())
                .setHubId(motion.getHubId())
                .setTimestamp(motion.getTimestamp())
                .setPayload(payload)
                .build();
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}