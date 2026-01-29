package wkallil.microservice.orderService.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wkallil.microservice.orderService.controller.OrderController;
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
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    public EntityModel<OrderResponseDto> createOrder(CreateOrderRequestDto requestDto) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerName(requestDto.getCustomerName());
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

        OrderResponseDto response = orderMapper.toResponse(savedOrder);
        return addHateoasLinks(response);
    }

    @Transactional(readOnly = true)
    public EntityModel<OrderResponseDto> getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        OrderResponseDto response = orderMapper.toResponse(order);
        return addHateoasLinks(response);
    }

    @Transactional(readOnly = true)
    public EntityModel<OrderResponseDto> getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with order number: " + orderNumber));

        OrderResponseDto response = orderMapper.toResponse(order);
        return addHateoasLinks(response);
    }

    @Transactional(readOnly = true)
    public PagedModel<EntityModel<OrderResponseDto>> getAllOrders(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);

        List<EntityModel<OrderResponseDto>> orders = orderPage.getContent().stream()
                .map(
                        order -> {
                            OrderResponseDto response = orderMapper.toResponse(order);
                            return addHateoasLinks(response);
                        }
                )
                .collect(Collectors.toList());

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                orderPage.getSize(),
                orderPage.getNumber(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages()
        );

        PagedModel<EntityModel<OrderResponseDto>> pagedModel = PagedModel.of(orders, metadata);

        pagedModel.add(linkTo(methodOn(OrderController.class)
                .getAllOrders(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()))
                .withSelfRel());

        if (orderPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(OrderController.class)
                    .getAllOrders(pageable.getPageNumber() + 1, pageable.getPageSize(), pageable.getSort()))
                    .withRel("next"));
        }

        if (orderPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(OrderController.class)
                    .getAllOrders(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort()))
                    .withRel("prev"));
        }

        pagedModel.add(linkTo(methodOn(OrderController.class)
                .getAllOrders(0, pageable.getPageSize(), pageable.getSort()))
                .withRel("first"));

        pagedModel.add(linkTo(methodOn(OrderController.class)
                .getAllOrders(orderPage.getTotalPages() - 1, pageable.getPageSize(), pageable.getSort()))
                .withRel("last"));

        return pagedModel;
    }

    @Transactional(readOnly = true)
    public PagedModel<EntityModel<OrderResponseDto>> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByStatus(status, pageable);

        List<EntityModel<OrderResponseDto>> orders = orderPage.getContent().stream()
                .map(
                        order -> {
                            OrderResponseDto response = orderMapper.toResponse(order);
                            return addHateoasLinks(response);
                        }
                )
                .collect(Collectors.toList());

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                orderPage.getSize(),
                orderPage.getNumber(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages()
        );

        PagedModel<EntityModel<OrderResponseDto>> pagedModel = PagedModel.of(orders, metadata);

        pagedModel.add(linkTo(methodOn(OrderController.class)
                .getOrdersByStatus(status, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()))
                .withSelfRel());

        if (orderPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(OrderController.class)
                    .getOrdersByStatus(status, pageable.getPageNumber() + 1, pageable.getPageSize(), pageable.getSort()))
                    .withRel("next"));
        }

        if (orderPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(OrderController.class)
                    .getOrdersByStatus(status, pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort()))
                    .withRel("prev"));
        }

        return pagedModel;
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

    private EntityModel<OrderResponseDto> addHateoasLinks(OrderResponseDto response) {
        EntityModel<OrderResponseDto> entityModel = EntityModel.of(response);

        entityModel.add(linkTo(methodOn(OrderController.class)
                .getOrderById(response.getId()))
                .withSelfRel());


        entityModel.add(linkTo(methodOn(OrderController.class)
                .getOrderByNumber(response.getOrderNumber()))
                .withRel("by-number"));


        entityModel.add(linkTo(methodOn(OrderController.class)
                .getAllOrders(0, 20, null))
                .withRel("all-orders"));


        entityModel.add(linkTo(methodOn(OrderController.class)
                .getOrdersByStatus(response.getStatus(), 0, 20, null))
                .withRel("same-status"));

        return entityModel;
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
