package wkallil.microservice.inventoryService.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@Schema(description = "Request to update inventory quantity")
@JsonPropertyOrder({"availableQuantity"})
public class UpdateInventoryRequestDto {

    @Schema(description = "New available quantity", example = "150", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0")
    @NotBlank(message = "Available quantity is required")
    @Min(value = 0, message = "Available quantity cannot be negative")
    private Integer availableQuantity;

    public UpdateInventoryRequestDto() {
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
        UpdateInventoryRequestDto that = (UpdateInventoryRequestDto) o;
        return Objects.equals(availableQuantity, that.availableQuantity);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(availableQuantity);
    }
}
