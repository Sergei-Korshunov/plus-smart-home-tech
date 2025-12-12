package ru.yandex.practicum.service.hub;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventService {
    void perform(HubEventAvro hubEventAvro);

    String getType();
}