package wkallil.microservice.orderService.dto.kafkaDto;

import java.util.Objects;

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
