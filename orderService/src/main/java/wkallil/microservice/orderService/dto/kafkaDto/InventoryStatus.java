package wkallil.microservice.orderService.dto.kafkaDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Inventory check status")
public enum InventoryStatus {
    @Schema(description = "Item exists and quantity is sufficient")
    AVAILABLE,

    @Schema(description = "Item exists but quantity is insufficient")
    PARTIALLY_AVAILABLE,

    @Schema(description = "Item does not exist in inventory")
    UNAVAILABLE
}
