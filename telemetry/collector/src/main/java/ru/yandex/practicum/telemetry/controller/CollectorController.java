package ru.yandex.practicum.telemetry.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.Json;
import ru.yandex.practicum.telemetry.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.service.CollectorService;
import ru.yandex.practicum.telemetry.service.CollectorServiceImpl;

@Slf4j
@RestController
@RequestMapping(path = "/events")
public class CollectorController {

    private final CollectorService eventService;

    @Autowired
    public CollectorController(CollectorServiceImpl eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sensor(@Valid @RequestBody SensorEvent sensorEvent) {
        eventService.sensorEvent(sensorEvent);

        log.info("1. Запрос на отправление события: тип: {} данные: {}", sensorEvent.getType(), Json.simpleObjectToJson(sensorEvent));
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void hub(@Valid @RequestBody HubEvent hubEvent) {
        eventService.hubEvent(hubEvent);
        log.info("1. Запрос на отправление события: тип: {} данные: {}", hubEvent.getType(), Json.simpleObjectToJson(hubEvent));
    }
}
