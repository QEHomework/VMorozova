package com.example;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MessageController {

    @Autowired
    private ConfigService configService;
    @Autowired
    private ErrorMetrics errorMetrics;
    @Autowired
    private ApiMetrics apiMetrics;

    private final MeterRegistry meterRegistry;
    private final ClientRepository clientRepository;
    private final KafkaProducerService kafkaProducerService;

    public MessageController(ClientRepository clientRepository, KafkaProducerService kafkaProducerService, MeterRegistry meterRegistry) {
        this.clientRepository = clientRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.meterRegistry = meterRegistry;
    }

    @PostMapping("/message")
    public ResponseEntity<MessageResponse> handleMessage(@RequestBody MessageRequest request) throws InterruptedException {
        Timer.Sample sample = Timer.start(meterRegistry);
        // Начинаем измерение общего времени выполнения
        Timer.Sample totalRequestSample = Timer.start(meterRegistry);

        log.debug("Handling message for clientId: {}", request.getClientId());

        int delay = configService.getServiceDelay("message");
        log.info("Applying delay of {} ms for service 'message'", delay);
        Thread.sleep(delay);

        int errorRate = configService.getServiceErrorRate("message");

        if (Math.random() * 100 < errorRate) {
            errorMetrics.incrementErrorCount();
            log.error("Error occurred while processing message for clientId: {}", request.getClientId());
            sample.stop(apiMetrics.getRequestTimer()); // Останавливаем таймер
            totalRequestSample.stop(apiMetrics.getTotalRequestTimer()); // Останавливаем общий таймер
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (request.getClientId() == null || request.getMessage() == null) {
            log.warn("Invalid request received: clientId or message is null");
            sample.stop(apiMetrics.getRequestTimer()); // Останавливаем таймер
            totalRequestSample.stop(apiMetrics.getTotalRequestTimer()); // Останавливаем общий таймер
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Client client = clientRepository.findById(request.getClientId()).orElse(null);

        Timer.Sample kafkaDeliverySample = Timer.start(meterRegistry);

        if (client != null) {
            EnrichedMessage enrichedMessage = new EnrichedMessage(
                    request.getClientId(),
                    request.getMessage(),
                    new ClientData(client.getName(), client.getEmail(), client.getPhone())
            );
            kafkaProducerService.sendMessage("enriched-messages-topic", enrichedMessage);
            log.info("Message enriched and sent to Kafka for clientId: {}", request.getClientId());
            return new ResponseEntity<>(new MessageResponse("success", "Message enriched and sent to Kafka"), HttpStatus.OK);
        } else {
            kafkaProducerService.sendMessage("raw-messages-topic", request);
            log.info("Message sent to Kafka without enrichment for clientId: {}", request.getClientId());
            sample.stop(apiMetrics.getRequestTimer()); // Останавливаем таймер
            // Останавливаем таймер доставки в Kafka
            kafkaDeliverySample.stop(apiMetrics.getKafkaDeliveryTimer());
            // Останавливаем общий таймер
            totalRequestSample.stop(apiMetrics.getTotalRequestTimer());
            return new ResponseEntity<>(new MessageResponse("success", "Message sent to Kafka without enrichment"), HttpStatus.OK);
        }
    }
}