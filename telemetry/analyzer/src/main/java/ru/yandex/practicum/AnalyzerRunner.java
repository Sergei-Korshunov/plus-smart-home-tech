package ru.yandex.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.processor.hub.HubEventProcessor;
import ru.yandex.practicum.processor.snapshot.SnapshotProcessor;

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
        Thread hubProcessorThread = new Thread(hubEventProcessor);
        hubProcessorThread.setName("HubEventProcessorThread");
        hubProcessorThread.start();

        snapshotProcessor.start();
    }
}