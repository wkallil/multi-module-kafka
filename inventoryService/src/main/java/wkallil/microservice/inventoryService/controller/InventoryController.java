package wkallil.microservice.inventoryService.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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
    public ResponseEntity<EntityModel<InventoryResponseDto>> createInventory(@Valid @RequestBody CreateInventoryRequestDto request) {
        EntityModel<InventoryResponseDto> response = inventoryService.createInventory(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{productCode}")
    @Override
    public ResponseEntity<EntityModel<InventoryResponseDto>> getInventoryByProductCode(@PathVariable String productCode) {
        EntityModel<InventoryResponseDto> response = inventoryService.getInventoryByProductCode(productCode);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<PagedModel<EntityModel<InventoryResponseDto>>> getAllInventories(@RequestParam(defaultValue = "0") int page,
                                                                                           @RequestParam(defaultValue = "5") int size,
                                                                                           @RequestParam(required = false) Sort sort) {
        Pageable pageable = sort != null
                ? PageRequest.of(page, size, sort)
                : PageRequest.of(page, size, Sort.by("productCode").ascending());

        PagedModel<EntityModel<InventoryResponseDto>> inventory = inventoryService.getAllInventories(pageable);
        return ResponseEntity.ok(inventory);
    }

    @PutMapping("/{productCode}")
    @Override
    public ResponseEntity<EntityModel<InventoryResponseDto>> updateInventory(@PathVariable String productCode,
                                                                @Valid @RequestBody UpdateInventoryRequestDto request) {
        EntityModel<InventoryResponseDto> response = inventoryService.updateInventory(productCode, request);

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
    public ResponseEntity<PagedModel<EntityModel<BackorderResponseDto>>> getAllBackorders(@RequestParam(defaultValue = "0") int page,
                                                                                          @RequestParam(defaultValue = "5") int size,
                                                                                          @RequestParam(required = false) Sort sort) {
        Pageable pageable = sort != null
                ? PageRequest.of(page, size, sort)
                : PageRequest.of(page, size, Sort.by("createdAt").descending());

        PagedModel<EntityModel<BackorderResponseDto>> backorders = inventoryService.getAllBackorders(pageable);
        return ResponseEntity.ok(backorders);
    }

    @GetMapping("/backorders/order/{orderNumber}")
    @Override
    public ResponseEntity<PagedModel<EntityModel<BackorderResponseDto>>> getBackordersByOrderNumber(@PathVariable String orderNumber,
                                                                                                    @RequestParam(defaultValue = "0") int page,
                                                                                                    @RequestParam(defaultValue = "5") int size,
                                                                                                    @RequestParam(required = false) Sort sort) {
        Pageable pageable = sort != null
                ? PageRequest.of(page, size, sort)
                : PageRequest.of(page, size, Sort.by("createdAt").descending());

        PagedModel<EntityModel<BackorderResponseDto>> backorders = inventoryService.getBackordersByOrderNumber(orderNumber,pageable);

        return ResponseEntity.ok(backorders);
    }
}
