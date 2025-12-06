package ru.yandex.practicum.telemetry.service.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.kafka.KafkaClient;
import ru.yandex.practicum.telemetry.mapper.protobuf.sensor.SensorEventMapper;

import java.util.Set;

@Component
public class SwitchSensorService extends SensorEventService {

    public SwitchSensorService(KafkaClient kafkaClient, Set<SensorEventMapper> sensorEventMappers) {
        super(kafkaClient, sensorEventMappers);
    }

    @Override
    public void push(SensorEventProto sensorEventProto) {
        SensorEventMapper sensorEventMapper = getMapper(sensorEventProto);

        if (sensorEventMapper != null) {
            KafkaClient kafkaClient = getKafkaClient();
            kafkaClient.sendData(kafkaClient.getKafkaProperty().getNameTopicSensor(),
                    null,
                    sensorEventProto.getTimestamp().getSeconds(), // ???
                    sensorEventProto.getHubId(),
                    sensorEventMapper.mapSimpleObjectToProto(sensorEventProto));
        }
    }

    @Override
    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }
}