package ru.yandex.practicum.telemetry.aggregator.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class SnapshotService {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        String hubId = event.getHubId();

        if (!snapshots.containsKey(hubId)) {
            Map<String, SensorStateAvro> sensorStates = new HashMap<>();
            sensorStates.put(event.getId(), createSensorState(event));

            SensorsSnapshotAvro snapshot = SensorsSnapshotAvro.newBuilder()
                    .setHubId(event.getHubId())
                    .setTimestamp(event.getTimestamp())
                    .setSensorsState(sensorStates)
                    .build();

            snapshots.put(hubId, snapshot);

            return Optional.of(snapshot);
        } else {
            SensorsSnapshotAvro oldSnapshot = snapshots.get(hubId);
            Optional<SensorsSnapshotAvro> updatedSnapshot = updateSnapshot(oldSnapshot, event);
            updatedSnapshot.ifPresent(sensorsSnapshotAvro ->
                    snapshots.put(hubId, sensorsSnapshotAvro));

            return updatedSnapshot;
        }
    }

    private SensorStateAvro createSensorState(SensorEventAvro event) {
        return SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
    }

    private Optional<SensorsSnapshotAvro> updateSnapshot(SensorsSnapshotAvro oldSnapshot, SensorEventAvro event) {
        Map<String, SensorStateAvro> sensorState = oldSnapshot.getSensorsState();
        boolean isExistSensorState = sensorState.containsKey(event.getId());
        SensorStateAvro sensorStateAvro = isExistSensorState ? sensorState.get(event.getId()) : null;

        if (isExistSensorState && (sensorStateAvro.getTimestamp().isAfter(event.getTimestamp())
                || sensorStateAvro.getData().equals(event.getPayload())))
            return Optional.empty();

        oldSnapshot.getSensorsState().put(event.getId(), createSensorState(event));
        oldSnapshot.setTimestamp(event.getTimestamp());

        return Optional.of(oldSnapshot);
    }
}