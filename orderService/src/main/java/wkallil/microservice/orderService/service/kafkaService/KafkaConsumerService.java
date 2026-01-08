package wkallil.microservice.orderService.service.kafkaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import wkallil.microservice.orderService.dto.kafkaDto.InventoryResponseDto;
import wkallil.microservice.orderService.service.OrderService;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final OrderService orderService;

    public KafkaConsumerService(OrderService orderService) {
        this.orderService = orderService;
    }


    @KafkaListener(topics = "${kafka.topics.inventory-response}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeInventoryResponse(InventoryResponseDto response) {
        logger.info("Received inventory response for order: {} with status: {}",
                response.getOrderNumber(), response.getStatus());

        try {
            orderService.updateOrderStatus(response.getOrderNumber(), response);
            logger.info("Order status updated successfully for order: {}", response.getOrderNumber());
        } catch (Exception e) {
            logger.error("Error updating order status: {}", e.getMessage(), e);
        }
    }
}
