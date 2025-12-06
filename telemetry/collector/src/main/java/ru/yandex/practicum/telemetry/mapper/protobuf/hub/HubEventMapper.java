package ru.yandex.practicum.telemetry.mapper.protobuf.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventMapper {

    SpecificRecordBase mapSimpleObjectToProto(HubEventProto event);

    HubEventProto.PayloadCase getType();
}