package ru.yandex.practicum.service.hub;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.repository.ScenarioRepository;

@Slf4j
@Service
public class ScenarioRemovedService implements HubEventService {
    private final ScenarioRepository scenarioRepository;

    @Autowired
    public ScenarioRemovedService(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @Transactional
    @Override
    public void perform(HubEventAvro hubEventAvro) {
        ScenarioRemovedEventAvro scenarioRemovedEventAvro = (ScenarioRemovedEventAvro) hubEventAvro.getPayload();
        String hubId = hubEventAvro.getHubId();
        String nameScenario = scenarioRemovedEventAvro.getName();

        try {
            scenarioRepository.findByHubIdAndName(hubId, nameScenario)
                    .ifPresent(scenario -> {
                        scenarioRepository.delete(scenario);
                        log.info("Удален сценарий из хаба по id {} с именем {}", hubId, nameScenario);
                    });
        } catch (Exception e) {
            log.error("Ошибка! Не удалось удалить сценарий из хаба по id {} с именем {}", hubId, nameScenario, e);
        }
    }

    @Override
    public String getType() {
        return ScenarioRemovedEventAvro.class.getSimpleName();
    }
}