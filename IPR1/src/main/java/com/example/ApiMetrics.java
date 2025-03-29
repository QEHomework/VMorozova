package com.example;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ApiMetrics {
    private final MeterRegistry meterRegistry;
    private Timer requestTimer;
    private final Timer totalRequestTimer;
    private final Timer kafkaDeliveryTimer;

    @Autowired
    public ApiMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        // Таймер для общего времени выполнения запроса
        this.totalRequestTimer = Timer.builder("api_total_request_duration_seconds")
                .description("Total time taken to process API request and deliver to Kafka")
                .register(meterRegistry);

        // Таймер для времени доставки в Kafka
        this.kafkaDeliveryTimer = Timer.builder("api_kafka_delivery_duration_seconds")
                .description("Time taken to deliver message to Kafka")
                .register(meterRegistry);
    }

    @PostConstruct
    public void init() {
        // Инициализация таймера для измерения времени отклика
        requestTimer = Timer.builder("api_request_duration_seconds")
                .description("Time taken to process API request")
                .register(meterRegistry);
    }

    public Timer getRequestTimer() {
        return requestTimer;
    }

    public Timer getTotalRequestTimer() {
        return totalRequestTimer;
    }

    public Timer getKafkaDeliveryTimer() {
        return kafkaDeliveryTimer;
    }
}
