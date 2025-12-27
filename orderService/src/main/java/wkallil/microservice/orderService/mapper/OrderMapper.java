package wkallil.microservice.orderService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import wkallil.microservice.orderService.dto.kafkaDto.OrderCreatedEventDto;
import wkallil.microservice.orderService.dto.kafkaDto.OrderItemEventDto;
import wkallil.microservice.orderService.dto.requestDto.CreateOrderRequestDto;
import wkallil.microservice.orderService.dto.requestDto.OrderItemRequestDto;
import wkallil.microservice.orderService.dto.responseDto.OrderItemResponseDto;
import wkallil.microservice.orderService.dto.responseDto.OrderResponseDto;
import wkallil.microservice.orderService.model.Order;
import wkallil.microservice.orderService.model.OrderItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toEntity(CreateOrderRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "totalPrice", expression = "java(requestDto.getUnitPrice().multiply(java.math.BigDecimal.valueOf(requestDto.getQuantity())))")
    OrderItem toEntity(OrderItemRequestDto requestDto);

    OrderResponseDto toResponse(Order order);

    OrderItemResponseDto toResponse(OrderItem orderItem);

    List<OrderResponseDto> toResponseList(List<Order> orders);

    @Mapping(target = "items", source = "order.items")
    OrderCreatedEventDto toOrderCreatedEvent(Order order);

    OrderItemEventDto toOrderItemEvent(OrderItem orderItem);
}
