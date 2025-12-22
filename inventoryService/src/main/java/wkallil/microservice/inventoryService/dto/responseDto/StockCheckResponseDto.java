package wkallil.microservice.inventoryService.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder({"productCode", "available", "availableQuantity", "requestedQuantity", "message"})
public class StockCheckResponseDto {

    private String productCode;
    private boolean available;
    private Integer availableQuantity;
    private Integer requestedQuantity;
    private String message;

    public StockCheckResponseDto() {
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(Integer requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
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
        StockCheckResponseDto that = (StockCheckResponseDto) o;
        return available == that.available && Objects.equals(productCode, that.productCode) && Objects.equals(availableQuantity, that.availableQuantity) && Objects.equals(requestedQuantity, that.requestedQuantity) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCode, available, availableQuantity, requestedQuantity, message);
    }
}
