package wkallil.microservice.orderService.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import wkallil.microservice.orderService.dto.kafkaDto.InventoryResponseDto;
import wkallil.microservice.orderService.dto.kafkaDto.InventoryStatus;
import wkallil.microservice.orderService.dto.kafkaDto.OrderCreatedEventDto;
import wkallil.microservice.orderService.dto.requestDto.CreateOrderRequestDto;
import wkallil.microservice.orderService.dto.requestDto.OrderItemRequestDto;
import wkallil.microservice.orderService.dto.responseDto.OrderResponseDto;
import wkallil.microservice.orderService.mapper.OrderMapper;
import wkallil.microservice.orderService.model.Order;
import wkallil.microservice.orderService.model.OrderItem;
import wkallil.microservice.orderService.model.OrderStatus;
import wkallil.microservice.orderService.repository.OrderRepository;
import wkallil.microservice.orderService.service.kafkaService.KafkaProducerService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        OrderItem orderItem = new OrderItem();
        orderItem.setProductCode("PROD-001");
        orderItem.setProductName("Test Product");
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(new BigDecimal("10.00"));
        orderItem.setTotalPrice(new BigDecimal("20.00"));

        order = new Order();
        order.setId(1L);
        order.setOrderNumber("ORD-12345678");
        order.setCustomerName("John Doe");
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("20.00"));
        order.setItems(new ArrayList<>(List.of(orderItem)));

        orderResponse = new OrderResponseDto();
        orderResponse.setId(1L);
        orderResponse.setOrderNumber("ORD-12345678");
        orderResponse.setStatus(OrderStatus.PENDING);
    }

    @Test
    void testCreateOrder_success() {
        OrderItem orderItem = new OrderItem();
        orderItem.setProductCode("PROD-001");
        orderItem.setProductName("Test Product");
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(new BigDecimal("10.00"));
        orderItem.setTotalPrice(new BigDecimal("20.00"));

        when(orderMapper.toEntity(any(OrderItemRequestDto.class))).thenReturn(orderItem);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toOrderCreatedEvent(any(Order.class))).thenReturn(new OrderCreatedEventDto());
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

        EntityModel<OrderResponseDto> result = orderService.createOrder(createOrderRequest);

        assertNotNull(result);
        assertEquals("ORD-12345678", result.getContent().getOrderNumber());
        assertEquals(OrderStatus.PENDING, result.getContent().getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(kafkaProducerService, times(1)).sendOrderCreatedEvent(any(OrderCreatedEventDto.class));
    }

    @Test
    void testGetOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        EntityModel<OrderResponseDto> result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getContent().getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.getOrderById(999L));
    }

    @Test
    void testUpdateOrderStatus_ToApproved() {
        InventoryResponseDto inventoryResponse = new InventoryResponseDto();
        inventoryResponse.setOrderNumber("ORD-12345678");
        inventoryResponse.setStatus(InventoryStatus.AVAILABLE);

        when(orderRepository.findByOrderNumber("ORD-12345678")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.updateOrderStatus("ORD-12345678", inventoryResponse);

        verify(orderRepository, times(1)).save(order);
        assertEquals(OrderStatus.APPROVED, order.getStatus());
    }

    @Test
    void testUpdateOrderStatus_ToOnHold() {
        InventoryResponseDto inventoryResponse = new InventoryResponseDto();
        inventoryResponse.setOrderNumber("ORD-12345678");
        inventoryResponse.setStatus(InventoryStatus.PARTIALLY_AVAILABLE);

        when(orderRepository.findByOrderNumber("ORD-12345678")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.updateOrderStatus("ORD-12345678", inventoryResponse);

        verify(orderRepository, times(1)).save(order);
        assertEquals(OrderStatus.ON_HOLD, order.getStatus());
    }

    @Test
    void testUpdateOrderStatus_ToRejected() {
        InventoryResponseDto inventoryResponse = new InventoryResponseDto();
        inventoryResponse.setOrderNumber("ORD-12345678");
        inventoryResponse.setStatus(InventoryStatus.UNAVAILABLE);

        when(orderRepository.findByOrderNumber("ORD-12345678")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.updateOrderStatus("ORD-12345678", inventoryResponse);

        verify(orderRepository, times(1)).save(order);
        assertEquals(OrderStatus.REJECTED, order.getStatus());
    }
}
