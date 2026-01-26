package wkallil.microservice.inventoryService.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wkallil.microservice.inventoryService.controller.docs.InventoryControllerDocs;
import wkallil.microservice.inventoryService.dto.requestDto.CreateInventoryRequestDto;
import wkallil.microservice.inventoryService.dto.requestDto.UpdateInventoryRequestDto;
import wkallil.microservice.inventoryService.dto.responseDto.BackorderResponseDto;
import wkallil.microservice.inventoryService.dto.responseDto.InventoryResponseDto;
import wkallil.microservice.inventoryService.dto.responseDto.StockCheckResponseDto;
import wkallil.microservice.inventoryService.service.InventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory Management", description = "APIs for managing inventory and stock levels")
public class InventoryController implements InventoryControllerDocs {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    @Override
    public ResponseEntity<InventoryResponseDto> createInventory(@Valid @RequestBody CreateInventoryRequestDto request) {
        InventoryResponseDto response = inventoryService.createInventory(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{productCode}")
    @Override
    public ResponseEntity<InventoryResponseDto> getInventoryByProductCode(@PathVariable String productCode) {
        InventoryResponseDto response = inventoryService.getInventoryByProductCode(productCode);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<InventoryResponseDto>> getAllInventories() {
        List<InventoryResponseDto> inventory = inventoryService.getAllInventories();

        return ResponseEntity.ok(inventory);
    }

    @PutMapping("/{productCode}")
    @Override
    public ResponseEntity<InventoryResponseDto> updateInventory(@PathVariable String productCode,
                                                                @Valid @RequestBody UpdateInventoryRequestDto request) {
        InventoryResponseDto response = inventoryService.updateInventory(productCode, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productCode}/check")
    @Override
    public ResponseEntity<StockCheckResponseDto> checkStock(@PathVariable String productCode,
                                                            @RequestParam Integer quantity) {
        StockCheckResponseDto response = inventoryService.checkStock(productCode, quantity);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/backorders")
    @Override
    public ResponseEntity<List<BackorderResponseDto>> getAllBackorders() {
        List<BackorderResponseDto> backorders = inventoryService.getAllBackorders();

        return ResponseEntity.ok(backorders);
    }

    @GetMapping("/backorders/order/{orderNumber}")
    @Override
    public ResponseEntity<List<BackorderResponseDto>> getBackordersByOrderNumber(@PathVariable String orderNumber) {
        List<BackorderResponseDto> backorders = inventoryService.getBackordersByOrderNumber(orderNumber);

        return ResponseEntity.ok(backorders);
    }
}
