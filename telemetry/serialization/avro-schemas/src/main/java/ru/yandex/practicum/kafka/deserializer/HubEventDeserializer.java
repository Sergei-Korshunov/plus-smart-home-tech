package ru.yandex.practicum.kafka.deserializer;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public class HubEventDeserializer extends BaseDeserializerAvro<HubEventAvro> {

    public HubEventDeserializer() {
        super(HubEventAvro.getClassSchema());
    }
}