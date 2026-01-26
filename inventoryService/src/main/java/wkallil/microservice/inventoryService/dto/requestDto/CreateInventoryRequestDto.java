package wkallil.microservice.inventoryService.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@Schema(description = "Request to create a new inventory item")
@JsonPropertyOrder({"productCode", "productName", "availableQuantity"})
public class CreateInventoryRequestDto {

    @Schema(description = "Unique product code", example = "PROD-009", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Product code is required")
    private String productCode;

    @Schema(description = "Product name", example = "Wireless Mouse", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Product name is required")
    private String productName;

    @Schema(description = "Initial available quantity", example = "100", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0")
    @NotBlank(message = "Available quantity is required")
    @Min(value = 0, message = "Available quantity cannot be negative")
    private Integer availableQuantity;

    public CreateInventoryRequestDto() {
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

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreateInventoryRequestDto that = (CreateInventoryRequestDto) o;
        return Objects.equals(productCode, that.productCode) && Objects.equals(productName, that.productName) && Objects.equals(availableQuantity, that.availableQuantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCode, productName, availableQuantity);
    }
}
