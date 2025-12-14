package ru.yandex.practicum.telemetry.mapper.protobuf.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.telemetry.mapper.protobuf.TimestampMapper;

@Component
public class SwitchSensorEventMapper implements SensorEventMapper {

    @Override
    public SpecificRecordBase mapSimpleObjectToProto(SensorEventProto event) {
        SwitchSensorProto switchSensorProto = event.getSwitchSensor();

        SwitchSensorAvro payload = SwitchSensorAvro.newBuilder()
                .setState(switchSensorProto.getState())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(TimestampMapper.toInstant(event.hasTimestamp() ? event.getTimestamp() : null))
                .setPayload(payload)
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }
}