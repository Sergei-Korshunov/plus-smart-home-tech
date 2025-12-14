package ru.yandex.practicum.service.hub;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.Json;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Service
public class DeviceRemovedService implements HubEventService {
    private final SensorRepository sensorRepository;

    @Autowired
    public DeviceRemovedService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Transactional
    @Override
    public void perform(HubEventAvro hubEventAvro) {
        DeviceRemovedEventAvro deviceRemovedAvro = (DeviceRemovedEventAvro) hubEventAvro.getPayload();

        try {
            sensorRepository.deleteByIdAndHubId(deviceRemovedAvro.getId(), hubEventAvro.getHubId());

            log.info("Удалено устройство из хаба с id {}, id устройства {} и данными {}",
                    hubEventAvro.getHubId(), deviceRemovedAvro.getId(), Json.avroObjectToJson(deviceRemovedAvro));
        } catch (Exception e) {
            log.error("Ошибка! Не удалось удалить устройство из хаба с id {}, id устройства {} и данными {}",
                    hubEventAvro.getHubId(), deviceRemovedAvro.getId(), Json.avroObjectToJson(deviceRemovedAvro), e);
        }
    }

    @Override
    public String getType() {
        return DeviceRemovedEventAvro.class.getSimpleName();
    }
}