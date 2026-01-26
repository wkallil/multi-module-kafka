package wkallil.microservice.orderService.dto.responseDto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Objects;

@Schema(description = "Order item details")
public class OrderItemResponseDto {

    @Schema(description = "Item ID", example = "1")
    private Long id;

    @Schema(description = "Product code", example = "PROD-001")
    private String productCode;

    @Schema(description = "Product name", example = "Laptop Dell XPS 13")
    private String productName;

    @Schema(description = "Quantity ordered", example = "2")
    private Integer quantity;

    @Schema(description = "Unit price", example = "1200.00")
    private BigDecimal unitPrice;

    @Schema(description = "Total price for this item", example = "2400.00")
    private BigDecimal totalPrice;

    public OrderItemResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemResponseDto that = (OrderItemResponseDto) o;
        return Objects.equals(id, that.id) && Objects.equals(productCode, that.productCode) && Objects.equals(productName, that.productName) && Objects.equals(quantity, that.quantity) && Objects.equals(unitPrice, that.unitPrice) && Objects.equals(totalPrice, that.totalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productCode, productName, quantity, unitPrice, totalPrice);
    }
}
