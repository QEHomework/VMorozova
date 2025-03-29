package com.example;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ErrorMetrics {

    private final Counter errorCounter;

    public ErrorMetrics(MeterRegistry registry) {
        this.errorCounter = Counter.builder("http_errors_total")
                .description("Total number of HTTP errors")
                .register(registry);
    }

    public void incrementErrorCount() {
        errorCounter.increment();
    }
}