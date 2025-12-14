package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.Json;
import ru.yandex.practicum.handler.BaseSensorEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.ConditionOperation;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SnapshotService {
    private final ClientService clientService;
    private final ScenarioRepository scenarioRepository;
    private final Map<String, BaseSensorEventHandler> sensorEventHandlers ;

    @Autowired
    public SnapshotService(ClientService clientService, ScenarioRepository scenarioRepository, Set<BaseSensorEventHandler> sensorEventHandlers) {
        this.clientService = clientService;
        this.scenarioRepository = scenarioRepository;
        this.sensorEventHandlers = sensorEventHandlers.stream().collect(Collectors.toMap(
                BaseSensorEventHandler::getType,
                Function.identity()
        ));
    }

    public void scenarioProcessing(SensorsSnapshotAvro sensorsSnapshot) {
        Map<String, SensorStateAvro> sensorStates = sensorsSnapshot.getSensorsState();
        List<Scenario> scenarios = scenarioRepository.findByHubId(sensorsSnapshot.getHubId());

        log.info("Обработка сценариев");
        scenarios.stream()
                .filter(scenario -> getAllScenarioConditions(scenario, sensorStates))
                .forEach(this::executeScenario);
    }

    private boolean getAllScenarioConditions(Scenario scenario, Map<String, SensorStateAvro> sensorStates) {
        Map<String, Condition> conditions = scenario.getConditions();

        log.info("Обработка условий для сценария {}", Json.simpleObjectToJson(scenario));
        return conditions.entrySet().stream().allMatch(
                entry -> processEachScenarioCondition(entry.getKey(), entry.getValue(), sensorStates));
    }

    private void executeScenario(Scenario scenario) {
        clientService.executeScenario(scenario);
    }

    private  boolean processEachScenarioCondition(String sensorId, Condition condition, Map<String, SensorStateAvro> sensorStates) {
        SensorStateAvro sensorStateAvro = sensorStates.get(sensorId);
        if (sensorStateAvro == null || sensorStateAvro.getData() == null) {
            log.debug("Нет данных для датчика с id {} в условии {}", sensorId, condition);
            return false;
        }

        String type = sensorStateAvro.getData().getClass().getName();
        log.info("Поиск обработчика для типа {}", type);
        if (!sensorEventHandlers.containsKey(type)) {
            log.warn("Не найден обработчик для датчика с типом {}", type);
            throw new IllegalArgumentException(String.format("Не найден обработчик для датчика с типом %s", type));
        }

        log.info("Обработчик для типа {} найден", type);
        log.info("Поиск значения для типа {}", type);
        BaseSensorEventHandler baseSensorEventHandler = sensorEventHandlers.get(type);
        Integer value = baseSensorEventHandler.getSensorValue(condition.getType(), sensorStateAvro);

        if (value == null) {
            log.warn("Значения для типа {} равно null", type);
            return false;
        }
        log.info("Значения для типа {} найдено", type);
        return checkCondition(condition, value);
    }

    private boolean checkCondition(Condition condition, Integer value) {
        log.info("Проверка значения по условиям");
        return switch (condition.getOperation()) {
            case ConditionOperation.LOWER_THAN -> value < condition.getValue();
            case ConditionOperation.EQUALS -> value.equals(condition.getValue());
            case ConditionOperation.GREATER_THAN -> value > condition.getValue();
        };
    }
}