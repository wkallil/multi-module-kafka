package wkallil.microservice.orderService.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Objects;

@JsonPropertyOrder("{customerName, items}")
public class CreateOrderRequestDto {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotEmpty(message = "Order must have at least one item")
    @Valid
    private List<OrderItemRequestDto> items;

    public CreateOrderRequestDto() {
    }

    public List<OrderItemRequestDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDto> items) {
        this.items = items;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreateOrderRequestDto that = (CreateOrderRequestDto) o;
        return Objects.equals(customerName, that.customerName) && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerName, items);
    }
}
