package wkallil.microservice.inventoryService.service.kafkaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import wkallil.microservice.inventoryService.dto.kafkaDto.InventoryResponseEventDto;
import wkallil.microservice.inventoryService.dto.kafkaDto.OrderCreatedEventDto;
import wkallil.microservice.inventoryService.service.InventoryService;

@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final InventoryService inventoryService;
    private final KafkaProducerService kafkaProducerService;

    public KafkaConsumerService(InventoryService inventoryService, KafkaProducerService kafkaProducerService) {
        this.inventoryService = inventoryService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @KafkaListener(topics = "${spring.kafka.topics.order-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeOrderCreated(OrderCreatedEventDto event) {
        logger.info("Received order created event for order: {}", event.getOrderNumber());

        try {
            InventoryResponseEventDto response = inventoryService.processOrderRequest(event);

            kafkaProducerService.sendInventoryResponse(response);

            logger.info("Successfully processed order: {} with status: {}", event.getOrderNumber(), response.getStatus());
        } catch (Exception e) {
            logger.error("Error processing order {}: {}", event.getOrderNumber(), e.getMessage(), e);
        }
    }
}
