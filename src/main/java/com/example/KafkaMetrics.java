package com.example;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class KafkaMetrics {

    private final Counter kafkaMessageCounter;

    public KafkaMetrics(MeterRegistry registry) {
        this.kafkaMessageCounter = Counter.builder("kafka_messages_sent")
                .description("Total number of messages sent to Kafka")
                .register(registry);
    }

    public void incrementMessageCount() {
        kafkaMessageCounter.increment();
    }
}