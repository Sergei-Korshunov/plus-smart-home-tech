package ru.yandex.practicum.service.hub;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.Json;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Service
public class DeviceAddedService implements HubEventService {
    private final SensorRepository sensorRepository;

    @Autowired
    public DeviceAddedService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Transactional
    @Override
    public void perform(HubEventAvro hubEventAvro) {
        DeviceAddedEventAvro deviceAddedAvro = (DeviceAddedEventAvro) hubEventAvro.getPayload();

        try {
            Sensor sensor = sensorRepository.save(
                    Sensor.builder()
                            .id(deviceAddedAvro.getId())
                            .hubId(hubEventAvro.getHubId())
                            .build()
            );
            log.info("Добавлено новое устройство в хаб с id {}, id устройство {} и данными {}",
                    hubEventAvro.getHubId(), deviceAddedAvro.getId(), Json.simpleObjectToJson(sensor));
        } catch (Exception e) {
            log.error("Ошибка! Не удалось добавить новое устройство в хаб с id {}, id устройство {}",
                    hubEventAvro.getHubId(), deviceAddedAvro.getId());
        }
    }

    @Override
    public String getType() {
        return DeviceAddedEventAvro.class.getSimpleName();
    }
}