package wkallil.microservice.orderService.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

@Schema(description = "Item details for an order")
@JsonPropertyOrder("{productCode, productName, quantity, unitPrice}")
public class OrderItemRequestDto {

    @Schema(description = "Product code", example = "PROD-001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Product code is required")
    private String productCode;

    @Schema(description = "Product name", example = "Laptop Dell XPS 13", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Product name is required")
    private String productName;

    @Schema(description = "Quantity to order", example = "2", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Schema(description = "Unit price of the product", example = "1200.00", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0.01")
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;

    public OrderItemRequestDto() {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemRequestDto that = (OrderItemRequestDto) o;
        return Objects.equals(productCode, that.productCode) && Objects.equals(productName, that.productName) && Objects.equals(quantity, that.quantity) && Objects.equals(unitPrice, that.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCode, productName, quantity, unitPrice);
    }
}
