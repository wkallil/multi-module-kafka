package wkallil.microservice.orderService.dto.responseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import wkallil.microservice.orderService.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Schema(description = "Order details response")
public class OrderResponseDto {

    @Schema(description = "Order ID", example = "1")
    private Long id;

    @Schema(description = "Unique order number", example = "ORD-123456")
    private String orderNumber;

    @Schema(description = "Customer name", example = "Gabrielle Oliveira")
    private String customerName;

    @Schema(description = "Current order status", example = "PENDING")
    private OrderStatus status;

    @Schema(description = "Total order amount", example = "2400.00")
    private BigDecimal totalAmount;

    @Schema(description = "List of items in the order")
    private List<OrderItemResponseDto> items;

    @Schema(description = "Order creation timestamp", example = "2024-06-15T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "Order last update timestamp", example = "2024-06-16T12:20:45")
    private LocalDateTime updatedAt;

    public OrderResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderItemResponseDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponseDto> items) {
        this.items = items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderResponseDto that = (OrderResponseDto) o;
        return Objects.equals(id, that.id) && Objects.equals(orderNumber, that.orderNumber) && Objects.equals(customerName, that.customerName) && status == that.status && Objects.equals(totalAmount, that.totalAmount) && Objects.equals(items, that.items) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderNumber, customerName, status, totalAmount, items, createdAt, updatedAt);
    }
}
