package ru.yandex.practicum.telemetry.aggregator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AggregatorRunner implements CommandLineRunner {

    private final AggregationStarter aggregationStarter;

    @Autowired
    public AggregatorRunner(AggregationStarter aggregationStarter) {
        this.aggregationStarter = aggregationStarter;
    }

    @Override
    public void run(String... args) throws Exception {
        aggregationStarter.start();
    }
}