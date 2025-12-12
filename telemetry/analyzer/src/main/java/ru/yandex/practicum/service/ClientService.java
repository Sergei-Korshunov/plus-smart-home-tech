package ru.yandex.practicum.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.Json;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequestProto;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.ActionType;
import ru.yandex.practicum.model.Scenario;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
public class ClientService {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    @Autowired
    public ClientService(@GrpcClient("hub-router")
                         HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void executeScenario(Scenario scenario) {
        log.info("Выполняю действия по сценарию {}", Json.simpleObjectToJson(scenario));
        String hubId = scenario.getHubId();
        String scenarioName = scenario.getName();
        Map<String, Action> actions = scenario.getActions();

        for (Map.Entry<String, Action> entries : actions.entrySet()) {
            DeviceActionRequestProto deviceActionRequestProto =
                    buildDeviceActionRequestProto(hubId, scenarioName, entries.getKey(), entries.getValue());

            hubRouterClient.handleDeviceAction(deviceActionRequestProto);
        }
    }

    private DeviceActionRequestProto buildDeviceActionRequestProto(String hubId, String scenarioName, String sensorId, Action action) {
        return DeviceActionRequestProto.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(buildDeviceActionProto(sensorId, action))
                .setTimestamp(currentTimestamp())
                .build();
    }

    private Timestamp currentTimestamp() {
        Instant instant = Instant.now();

        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    private DeviceActionProto buildDeviceActionProto(String sensorId, Action action) {
        DeviceActionProto.Builder actionBuilder = DeviceActionProto.newBuilder()
                .setSensorId(sensorId)
                .setType(ActionType.toProto(action.getType()));

        if (action.getValue() != null)
            actionBuilder.setValue(action.getValue());

        return actionBuilder.build();
    }
}