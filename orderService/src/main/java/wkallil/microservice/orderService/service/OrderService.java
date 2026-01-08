package wkallil.microservice.orderService.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wkallil.microservice.orderService.dto.kafkaDto.InventoryResponseDto;
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
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final KafkaProducerService kafkaProducerService;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, KafkaProducerService kafkaProducerService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Transactional
    public OrderResponseDto createOrder(CreateOrderRequestDto requestDto) {
        Order order = orderMapper.toEntity(requestDto);

        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequestDto itemRequest : requestDto.getItems()) {
            OrderItem item = orderMapper.toEntity(itemRequest);
            order.addItem(item);
            totalAmount = totalAmount.add(item.getTotalPrice());
        }
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        OrderCreatedEventDto event = orderMapper.toOrderCreatedEvent(savedOrder);
        kafkaProducerService.sendOrderCreatedEvent(event);

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with order number: " + orderNumber));

        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {
        return orderMapper.toResponseList(orderRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByStatus(OrderStatus status) {
        return orderMapper.toResponseList(orderRepository.findByStatus(status));
    }

    @Transactional
    public void updateOrderStatus(String orderNumber, InventoryResponseDto inventoryResponseDto) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with number: " + orderNumber));

        OrderStatus newStatus = switch (inventoryResponseDto.getStatus()) {
            case AVAILABLE -> OrderStatus.APPROVED;
            case PARTIALLY_AVAILABLE -> OrderStatus.ON_HOLD;
            case UNAVAILABLE -> OrderStatus.REJECTED;
        };

        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }
}
