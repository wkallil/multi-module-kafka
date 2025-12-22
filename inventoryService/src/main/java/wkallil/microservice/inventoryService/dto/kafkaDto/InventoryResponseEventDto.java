package wkallil.microservice.inventoryService.dto.kafkaDto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder({"orderNumber", "status", "message"})
public class InventoryResponseEventDto {

    private String orderNumber;
    private InventoryStatus status;
    private String message;

    public InventoryResponseEventDto() {
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
        InventoryResponseEventDto that = (InventoryResponseEventDto) o;
        return Objects.equals(orderNumber, that.orderNumber) && status == that.status && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderNumber, status, message);
    }
}
