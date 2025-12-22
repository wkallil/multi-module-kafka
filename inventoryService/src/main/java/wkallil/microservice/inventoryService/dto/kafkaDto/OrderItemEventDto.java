package wkallil.microservice.inventoryService.dto.kafkaDto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder({"productCode", "quantity"})
public class OrderItemEventDto {

    private String productCode;
    private Integer quantity;

    public OrderItemEventDto() {
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemEventDto that = (OrderItemEventDto) o;
        return Objects.equals(productCode, that.productCode) && Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCode, quantity);
    }
}
