package ru.yandex.practicum.telemetry.mapper.protobuf.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventMapper {

    SpecificRecordBase mapSimpleObjectToProto(SensorEventProto event);

    SensorEventProto.PayloadCase getType();
}