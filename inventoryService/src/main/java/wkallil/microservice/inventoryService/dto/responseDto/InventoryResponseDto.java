package wkallil.microservice.inventoryService.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Objects;

@Schema(description = "Inventory item details")
@JsonPropertyOrder({"id", "productCode", "productName", "availableQuantity", "reservedQuantity", "totalQuantity", "createdAt", "updatedAt"})
public class InventoryResponseDto {

    @Schema(description = "Inventory ID", example = "1")
    private Long id;

    @Schema(description = "Product Code", example = "PROD-001")
    private String productCode;

    @Schema(description = "Product name", example = "Laptop Dell XPS 13")
    private String productName;

    @Schema(description = "Quantity available for sale", example = "50")
    private Integer availableQuantity;

    @Schema(description = "Quantity reserved for pending orders", example = "10")
    private Integer reservedQuantity;

    @Schema(description = "Total quantity (available + reserved)", example = "60")
    private Integer totalQuantity;

    @Schema(description = "Creation timestamp", example = "2024-01-15T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-20T12:00:00")
    private LocalDateTime updatedAt;

    public InventoryResponseDto() {
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

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
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
        InventoryResponseDto that = (InventoryResponseDto) o;
        return Objects.equals(id, that.id) && Objects.equals(productCode, that.productCode) && Objects.equals(productName, that.productName) && Objects.equals(availableQuantity, that.availableQuantity) && Objects.equals(reservedQuantity, that.reservedQuantity) && Objects.equals(totalQuantity, that.totalQuantity) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productCode, productName, availableQuantity, reservedQuantity, totalQuantity, createdAt, updatedAt);
    }
}
