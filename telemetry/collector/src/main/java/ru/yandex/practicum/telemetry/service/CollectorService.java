package ru.yandex.practicum.telemetry.service;

import ru.yandex.practicum.telemetry.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.model.sensor.SensorEvent;

public interface CollectorService {
    void sensorEvent(SensorEvent sensorEvent);

    void hubEvent(HubEvent hubEvent);
}