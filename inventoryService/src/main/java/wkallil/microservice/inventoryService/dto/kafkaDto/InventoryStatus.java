package wkallil.microservice.inventoryService.dto.kafkaDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Inventory availability status")
public enum InventoryStatus {
    @Schema(description = "Item exists and quantity is sufficient")
    AVAILABLE,

    @Schema(description = "Item exists but quantity is insufficient")
    PARTIALLY_AVAILABLE,

    @Schema(description = "Item does not exists in inventory")
    UNAVAILABLE
}
