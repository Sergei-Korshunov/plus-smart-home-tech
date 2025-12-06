package ru.yandex.practicum.telemetry.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.service.hub.HubEventService;
import ru.yandex.practicum.telemetry.service.sensor.SensorEventService;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
public class CollectorController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<SensorEventProto.PayloadCase, SensorEventService> sensorEventService;
    private final Map<HubEventProto.PayloadCase, HubEventService> hubEventService;

    @Autowired
    public CollectorController(Set<SensorEventService> sensorEventServices,
                               Set<HubEventService> hubEventService) {
        this.sensorEventService = sensorEventServices.stream()
                .collect(Collectors.toMap(SensorEventService::getType, Function.identity()));
        this.hubEventService = hubEventService.stream()
                .collect(Collectors.toMap(HubEventService::getType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Посиск обработчика события для типа {}", request.getPayloadCase().name());
            if (sensorEventService.containsKey(request.getPayloadCase())) {
                log.info("Обработчик для события {} - найден", request.getPayloadCase().name());

                sensorEventService.get(request.getPayloadCase()).push(request);
            } else {
                log.info("Обработчик для события {} - не найден", request.getPayloadCase().name());
                throw new IllegalArgumentException(
                        String.format("Обработчик для события %s не найден", request.getPayloadCase()));
            }

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

            log.info("Обработка запроса завершена");
        } catch (Exception e) {
            log.error("Ошибка: {}", Status.fromThrowable(e));
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Посиск обработчика события для типа {}", request.getPayloadCase().name());
            if (hubEventService.containsKey(request.getPayloadCase())) {
                log.info("Обработчик для события {} - найден", request.getPayloadCase().name());

                hubEventService.get(request.getPayloadCase()).push(request);
            } else {
                log.info("Обработчик для события {} - не найден", request.getPayloadCase().name());
                throw new IllegalArgumentException(
                        String.format("Обработчик для события %s не найден", request.getPayloadCase()));
            }

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

            log.info("Обработка запроса завершена");
        } catch (Exception e) {
            log.error("Ошибка: {}", Status.fromThrowable(e));
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }
}