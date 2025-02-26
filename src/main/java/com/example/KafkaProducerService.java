package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Autowired
    private KafkaMetrics kafkaMetrics;
    public void sendMessage(String topic, Object message) {
        kafkaTemplate.send(topic, message);
        kafkaMetrics.incrementMessageCount();
    }
}