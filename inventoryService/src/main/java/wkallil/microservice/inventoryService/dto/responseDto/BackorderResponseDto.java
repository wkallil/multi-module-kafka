package wkallil.microservice.inventoryService.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonPropertyOrder({"id", "orderNumber", "productCode", "productName", "requestedQuantity", "missingQuantity", "status", "createdAt", "updatedAt"})
public class BackorderResponseDto {

    private Long id;
    private String orderNumber;
    private String productCode;
    private String productName;
    private Integer requestedQuantity;
    private Integer missingQuantity;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BackorderResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(Integer requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public Integer getMissingQuantity() {
        return missingQuantity;
    }

    public void setMissingQuantity(Integer missingQuantity) {
        this.missingQuantity = missingQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        BackorderResponseDto that = (BackorderResponseDto) o;
        return Objects.equals(id, that.id) && Objects.equals(orderNumber, that.orderNumber) && Objects.equals(productCode, that.productCode) && Objects.equals(productName, that.productName) && Objects.equals(requestedQuantity, that.requestedQuantity) && Objects.equals(missingQuantity, that.missingQuantity) && Objects.equals(status, that.status) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderNumber, productCode, productName, requestedQuantity, missingQuantity, status, createdAt, updatedAt);
    }
}
