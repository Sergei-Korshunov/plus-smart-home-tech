package ru.yandex.practicum.telemetry.mapper.protobuf.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.mapper.protobuf.TimestampMapper;

@Component
public class DeviceRemovedEventMapper implements HubEventMapper {

    @Override
    public SpecificRecordBase mapSimpleObjectToProto(HubEventProto event) {
        DeviceRemovedEventProto deviceRemovedEventProto = event.getDeviceRemoved();

        DeviceRemovedEventAvro payload = DeviceRemovedEventAvro.newBuilder()
                .setId(deviceRemovedEventProto.getId())
                .build();

        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(TimestampMapper.toInstant(event.hasTimestamp() ? event.getTimestamp() : null))
                .setPayload(payload)
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }
}