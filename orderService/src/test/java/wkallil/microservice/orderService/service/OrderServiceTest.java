package wkallil.microservice.orderService.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wkallil.microservice.orderService.dto.requestDto.CreateOrderRequestDto;
import wkallil.microservice.orderService.dto.requestDto.OrderItemRequestDto;
import wkallil.microservice.orderService.dto.responseDto.OrderResponseDto;
import wkallil.microservice.orderService.mapper.OrderMapper;
import wkallil.microservice.orderService.model.Order;
import wkallil.microservice.orderService.model.OrderStatus;
import wkallil.microservice.orderService.repository.OrderRepository;
import wkallil.microservice.orderService.service.kafkaService.KafkaProducerService;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private OrderService orderService;

    private CreateOrderRequestDto createOrderRequest;
    private Order order;
    private OrderResponseDto orderResponse;

    @BeforeEach
    void setUp() {
        OrderItemRequestDto itemRequest = new OrderItemRequestDto();
        itemRequest.setProductCode("PROD-001");
        itemRequest.setProductName("Test Product");
        itemRequest.setQuantity(2);
        itemRequest.setUnitPrice(new BigDecimal("10.00"));

        createOrderRequest = new CreateOrderRequestDto();
        createOrderRequest.setCustomerName("John Doe");
        createOrderRequest.setItems(List.of(itemRequest));

        order = new Order();
        order.setId(1L);
        order.setOrderNumber("ORD-12345678");
        order.setCustomerName("John Doe");
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("20.00"));

        orderResponse = new OrderResponseDto();
        orderResponse.setId(1L);
        orderResponse.setOrderNumber("ORD-12345678");
        orderResponse.setStatus(OrderStatus.PENDING);
    }
}
