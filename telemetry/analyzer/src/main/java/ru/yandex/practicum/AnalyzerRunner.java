package ru.yandex.practicum;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.processor.hub.HubEventProcessor;
import ru.yandex.practicum.processor.snapshot.SnapshotProcessor;

@Slf4j
@Component
public class AnalyzerRunner implements CommandLineRunner {
    private final HubEventProcessor hubEventProcessor;
    private final SnapshotProcessor snapshotProcessor;

    @Autowired
    public AnalyzerRunner(HubEventProcessor hubEventProcessor, SnapshotProcessor snapshotProcessor) {
        this.hubEventProcessor = hubEventProcessor;
        this.snapshotProcessor = snapshotProcessor;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Запуск сервиса Analyzer");
        Thread hubProcessorThread = new Thread(hubEventProcessor);
        hubProcessorThread.setName("HubEventProcessorThread");

        log.info("Запуск обработчика хаба");
        hubProcessorThread.start();

        log.info("Запуск обработчика снапшотов");
        snapshotProcessor.start();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Остановка сервиса Analyzer");

        try {
            hubEventProcessor.stop();
        } catch (Exception e) {
            log.warn("Не удалось корректно остановить обработчик хаба", e);
        }

        try {
            snapshotProcessor.stop();
        } catch (Exception e) {
            log.warn("Не удалось корректно остановить обработчик снапшотов", e);
        }
    }
}