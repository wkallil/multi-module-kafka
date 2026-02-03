package wkallil.microservice.inventoryService.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wkallil.microservice.inventoryService.dto.requestDto.CreateInventoryRequestDto;
import wkallil.microservice.inventoryService.dto.requestDto.UpdateInventoryRequestDto;
import wkallil.microservice.inventoryService.dto.responseDto.BackorderResponseDto;
import wkallil.microservice.inventoryService.dto.responseDto.InventoryResponseDto;
import wkallil.microservice.inventoryService.dto.responseDto.StockCheckResponseDto;

import java.util.List;

public interface InventoryControllerDocs {
    @Operation(
            summary = "Create inventory item",
            description = "Adds a new product to the inventory system."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Inventory item created successfully",
                            content = @Content(schema = @Schema(implementation = InventoryResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Product already exists"
                    )
            }
    )
    ResponseEntity<EntityModel<InventoryResponseDto>> createInventory(@Valid @RequestBody CreateInventoryRequestDto request);

    @Operation(
            summary = "Get inventory by product code",
            description = "Retrieves inventory details for a specific product."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Inventory found",
                            content = @Content(schema = @Schema(implementation = InventoryResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found"
                    )
            }
    )
    ResponseEntity<EntityModel<InventoryResponseDto>> getInventoryByProductCode(@PathVariable String productCode);

    @Operation(
            summary = "Get all inventory",
            description = "Retrieves a list of all inventory items."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of inventory items retrieved successfully"
                    )
            }
    )
    ResponseEntity<PagedModel<EntityModel<InventoryResponseDto>>> getAllInventories(@RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "5") int size,
                                                                                    @RequestParam(required = false) Sort sort);

    @Operation(
            summary = "Update inventory",
            description = "Updates the available quantity of a product in inventory."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Inventory updated successfully",
                            content = @Content(schema = @Schema(implementation = InventoryResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found"
                    )
            }
    )
    ResponseEntity<EntityModel<InventoryResponseDto>> updateInventory(@PathVariable String productCode,
                                                         @Valid @RequestBody UpdateInventoryRequestDto request);

    @Operation(
            summary = "Check stock availability",
            description = "Checks if a product has sufficient stock for the requested quantity."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Stock availability checked successfully",
                            content = @Content(schema = @Schema(implementation = StockCheckResponseDto.class))
                    )
            }
    )
    ResponseEntity<StockCheckResponseDto> checkStock(@PathVariable String productCode,
                                                     @RequestParam Integer quantity);

    @Operation(
            summary = "Get all backorders",
            description = "Retrieves a list of all pending backorders (items with insufficient stock)."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of backorders retrieved successfully"
                    )
            }
    )
    ResponseEntity<PagedModel<EntityModel<BackorderResponseDto>>> getAllBackorders(@RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "5") int size,
                                                                                   @RequestParam(required = false) Sort sort);

    @Operation(
            summary = "Get backorders by order number",
            description = "Retrieves backorders for a specific order."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Backorders for the order retrieved successfully"
                    )
            }
    )
    ResponseEntity<PagedModel<EntityModel<BackorderResponseDto>>> getBackordersByOrderNumber(@PathVariable String orderNumber,
                                                                                             @RequestParam(defaultValue = "0") int page,
                                                                                             @RequestParam(defaultValue = "5") int size,
                                                                                             @RequestParam(required = false) Sort sort);
}
