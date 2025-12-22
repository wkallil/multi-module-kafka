package wkallil.microservice.inventoryService.dto.kafkaDto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Objects;

@JsonPropertyOrder({"orderNumber", "items"})
public class OrderCreatedEventDto {

    private String orderNumber;
    private List<OrderItemEventDto> items;

    public OrderCreatedEventDto() {
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<OrderItemEventDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemEventDto> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderCreatedEventDto that = (OrderCreatedEventDto) o;
        return Objects.equals(orderNumber, that.orderNumber) && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderNumber, items);
    }
}
