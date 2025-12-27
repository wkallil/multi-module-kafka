package wkallil.microservice.orderService.dto.kafkaDto;

import java.util.Objects;

public class InventoryResponseDto {

    private String orderNumber;
    private InventoryStatus status;
    private String message;

    public InventoryResponseDto() {
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public InventoryStatus getStatus() {
        return status;
    }

    public void setStatus(InventoryStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InventoryResponseDto that = (InventoryResponseDto) o;
        return Objects.equals(orderNumber, that.orderNumber) && status == that.status && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderNumber, status, message);
    }
}
