package wkallil.microservice.inventoryService.service.kafkaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import wkallil.microservice.inventoryService.dto.kafkaDto.InventoryResponseEventDto;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, InventoryResponseEventDto> kafkaTemplate;

    @Value("${spring.kafka.topics.inventory-response}")
    private String inventoryResponseTopic;

    public KafkaProducerService(KafkaTemplate<String, InventoryResponseEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendInventoryResponse(InventoryResponseEventDto event) {
        logger.info("Sending inventory response for order: {} with status: {}", event.getOrderNumber(), event.getStatus());

        kafkaTemplate.send(inventoryResponseTopic, event.getOrderNumber(), event);
        logger.info("Inventory response sent successfully");
    }

}
