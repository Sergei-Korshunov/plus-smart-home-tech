package ru.yandex.practicum.service.hub;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class ScenarioAddedService implements HubEventService {
    private final ScenarioRepository scenarioRepository;
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;
    private final SensorRepository sensorRepository;

    @Autowired
    public ScenarioAddedService(ScenarioRepository scenarioRepository, ActionRepository actionRepository, ConditionRepository conditionRepository, SensorRepository sensorRepository) {
        this.scenarioRepository = scenarioRepository;
        this.actionRepository = actionRepository;
        this.conditionRepository = conditionRepository;
        this.sensorRepository = sensorRepository;
    }

    @Transactional
    @Override
    public void perform(HubEventAvro hubEventAvro) {
        ScenarioAddedEventAvro scenarioAddedEventAvro = (ScenarioAddedEventAvro) hubEventAvro.getPayload();
        String hubId = hubEventAvro.getHubId();

        try {
            chekSensorsExist(hubId, scenarioAddedEventAvro);

            Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, scenarioAddedEventAvro.getName())
                    .orElseGet(() -> Scenario.builder()
                            .hubId(hubId)
                            .name(scenarioAddedEventAvro.getName())
                            .conditions(new HashMap<>())
                            .actions(new HashMap<>())
                            .build());

            log.debug("Scenario: {}", scenario);

            scenario.getConditions().clear();
            scenario.getActions().clear();

            scenario.getConditions().putAll(buildConditionsHashMap(scenarioAddedEventAvro));
            scenario.getActions().putAll(buildActionsHashMap(scenarioAddedEventAvro));

            scenarioRepository.save(scenario);
            log.info("Добавлен новый сценарий в хаб с id {} и с именем {}", hubId, scenarioAddedEventAvro.getName());
        } catch (Exception e) {
            log.error("Ошибка! Не удалось добавить новый сценарий в хаб с id {} и с именем {}",
                    hubId, scenarioAddedEventAvro.getName(), e);
        }
    }

    @Override
    public String getType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }

    private void chekSensorsExist(String hubId, ScenarioAddedEventAvro scenarioAvro) {
        Set<String> sensorIds = new HashSet<>();
        scenarioAvro.getConditions().forEach(c -> sensorIds.add(c.getSensorId()));
        scenarioAvro.getActions().forEach(a -> sensorIds.add(a.getSensorId()));

        for (String sensorId : sensorIds) {
            sensorRepository.findById(sensorId).ifPresentOrElse(sensor -> {
                if (!hubId.equals(sensor.getHubId())) {
                    sensor.setHubId(hubId);
                    sensorRepository.save(sensor);
                }
            }, () -> sensorRepository.save(Sensor.builder()
                    .id(sensorId)
                    .hubId(hubId)
                    .build()));
        }
    }

    private Map<String, Condition> buildConditionsHashMap(ScenarioAddedEventAvro scenarioAddedEventAvro) {
        Map<String, Condition> conditions = new HashMap<>();
        for (ScenarioConditionAvro scenarioConditionAvro : scenarioAddedEventAvro.getConditions()) {
            Condition condition = Condition.builder()
                    .type(ConditionType.fromAvro(scenarioConditionAvro.getType()))
                    .operation(ConditionOperation.fromAvro(scenarioConditionAvro.getOperation()))
                    .value(resolveConditionValue(scenarioConditionAvro))
                    .build();
            Condition conditionsSaved = conditionRepository.save(condition);

            conditions.put(scenarioConditionAvro.getSensorId(), conditionsSaved);
        }
        return conditions;
    }

    private static Integer resolveConditionValue(ScenarioConditionAvro scenarioConditionAvro) {
        Object value = scenarioConditionAvro.getValue();

        return switch (value) {
            case null -> null;
            case Integer intValue -> intValue;
            case Boolean boolValue -> boolValue ? 1 : 0;
            default ->
                    throw new IllegalArgumentException(String.format("Неподдерживаемый тип значения условия: %s", value.getClass()));
        };
    }

    private Map<String, Action> buildActionsHashMap(ScenarioAddedEventAvro scenarioAddedEventAvro) {
        Map<String, Action> actions = new HashMap<>();
        for (DeviceActionAvro deviceActionAvro : scenarioAddedEventAvro.getActions()) {
            Action action = Action.builder()
                    .type(ActionType.fromAvro(deviceActionAvro.getType()))
                    .value(deviceActionAvro.getValue())
                    .build();
            Action actionsSaved = actionRepository.save(action);

            actions.put(deviceActionAvro.getSensorId(), actionsSaved);
        }
        return actions;
    }
}