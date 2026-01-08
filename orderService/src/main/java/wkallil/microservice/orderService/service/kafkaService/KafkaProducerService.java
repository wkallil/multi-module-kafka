package wkallil.microservice.orderService.service.kafkaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import wkallil.microservice.orderService.dto.kafkaDto.OrderCreatedEventDto;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, OrderCreatedEventDto> kafkaTemplate;

    @Value("${kafka.topics.order-created}")
    private String orderCreatedTopic;


    public KafkaProducerService(KafkaTemplate<String, OrderCreatedEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderCreatedEvent(OrderCreatedEventDto event) {
        logger.info("Sending order created event for order: {}", event.getOrderNumber());

        kafkaTemplate.send(orderCreatedTopic, event);
        logger.info("Order created event sent successfully");
    }
}
