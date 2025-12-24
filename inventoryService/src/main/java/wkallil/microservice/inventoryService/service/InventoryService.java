package wkallil.microservice.inventoryService.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wkallil.microservice.inventoryService.dto.kafkaDto.InventoryResponseEventDto;
import wkallil.microservice.inventoryService.dto.kafkaDto.InventoryStatus;
import wkallil.microservice.inventoryService.dto.kafkaDto.OrderCreatedEventDto;
import wkallil.microservice.inventoryService.dto.kafkaDto.OrderItemEventDto;
import wkallil.microservice.inventoryService.dto.requestDto.CreateInventoryRequestDto;
import wkallil.microservice.inventoryService.dto.requestDto.UpdateInventoryRequestDto;
import wkallil.microservice.inventoryService.dto.responseDto.BackorderResponseDto;
import wkallil.microservice.inventoryService.dto.responseDto.InventoryResponseDto;
import wkallil.microservice.inventoryService.dto.responseDto.StockCheckResponseDto;
import wkallil.microservice.inventoryService.mapper.InventoryMapper;
import wkallil.microservice.inventoryService.model.Backorder;
import wkallil.microservice.inventoryService.model.Inventory;
import wkallil.microservice.inventoryService.repository.BackorderRepository;
import wkallil.microservice.inventoryService.repository.InventoryRepository;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final BackorderRepository backorderRepository;
    private final InventoryMapper inventoryMapper;

    public InventoryService(InventoryRepository inventoryRepository, BackorderRepository backorderRepository, InventoryMapper inventoryMapper) {
        this.inventoryRepository = inventoryRepository;
        this.backorderRepository = backorderRepository;
        this.inventoryMapper = inventoryMapper;
    }

    @Transactional
    public InventoryResponseDto createInventory(CreateInventoryRequestDto requestDto) {
        if (inventoryRepository.existsByProductCode(requestDto.getProductCode())) {
            throw new RuntimeException("Product already exists " + requestDto.getProductCode());
        }

        Inventory inventory = inventoryMapper.toEntity(requestDto);
        Inventory savedInventory = inventoryRepository.save(inventory);

        return inventoryMapper.toResponse(savedInventory);
    }

    @Transactional(readOnly = true)
    public InventoryResponseDto getInventoryByProductCode(String productCode) {
        Inventory inventory = inventoryRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productCode));

        return inventoryMapper.toResponse(inventory);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponseDto> getAllInventories() {
        return inventoryMapper.toResponseList(inventoryRepository.findAll());
    }

    @Transactional
    public InventoryResponseDto updateInventory(String productCode, UpdateInventoryRequestDto requestDto) {
        Inventory inventory = inventoryRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productCode));

        inventoryMapper.updateInventoryFromRequest(requestDto, inventory);
        Inventory updatedInventory = inventoryRepository.save(inventory);

        return inventoryMapper.toResponse(updatedInventory);
    }

    @Transactional
    public StockCheckResponseDto checkStock(String productCode, Integer requestedQuantity) {
        StockCheckResponseDto responseDto = new StockCheckResponseDto();
        responseDto.setProductCode(productCode);
        responseDto.setRequestedQuantity(requestedQuantity);

        Inventory inventory = inventoryRepository.findByProductCode(productCode).orElse(null);

        if (inventory == null) {
            responseDto.setAvailable(false);
            responseDto.setAvailableQuantity(0);
            responseDto.setMessage("Product does not exist in inventory");

            return responseDto;
        }

        responseDto.setAvailableQuantity(inventory.getAvailableQuantity());

        if (inventory.hasEnoughStock(requestedQuantity)) {
            responseDto.setAvailable(true);
            responseDto.setMessage("Sufficient stock available");
        } else if (inventory.getAvailableQuantity() > 0) {
            responseDto.setAvailable(false);
            responseDto.setMessage("Insufficient stock. Available: " + inventory.getAvailableQuantity() + ", Requested: " + requestedQuantity);
        } else {
            responseDto.setAvailable(false);
            responseDto.setMessage("Product is out of stock");
        }

        return responseDto;
    }

    @Transactional
    public InventoryResponseEventDto processOrderRequest(OrderCreatedEventDto orderEventDto) {
        InventoryResponseEventDto responseDto = new InventoryResponseEventDto();
        responseDto.setOrderNumber(orderEventDto.getOrderNumber());


        boolean allItemsAvailable = true;
        boolean anyItemUnavailable = false;
        StringBuilder messageBuilder = new StringBuilder();

        for (OrderItemEventDto item : orderEventDto.getItems()) {
            Inventory inventory = inventoryRepository.findByProductCode(item.getProductCode()).orElse(null);

            if (inventory == null) {
                anyItemUnavailable = true;
                messageBuilder.append("Product ").append(item.getProductCode()).append(" does not exist. ");
            } else if (!inventory.hasEnoughStock(item.getQuantity())) {
                allItemsAvailable = false;

                createBackorder(orderEventDto.getOrderNumber(), inventory, item.getQuantity());

                messageBuilder.append("Product ").append(item.getProductCode())
                        .append(" has insufficient stock. Available: ")
                        .append(inventory.getAvailableQuantity())
                        .append(", Requested: ")
                        .append(item.getQuantity()).append(". ");
            } else {
                inventory.reserveStock(item.getQuantity());
                inventoryRepository.save(inventory);
            }
        }

        if (anyItemUnavailable) {
            responseDto.setStatus(InventoryStatus.UNAVAILABLE);
            responseDto.setMessage("One or more items do not exist in inventory. " + messageBuilder);
        } else if (!allItemsAvailable) {
            responseDto.setStatus(InventoryStatus.PARTIALLY_AVAILABLE);
            responseDto.setMessage("Items exist but insufficient quantity. " + messageBuilder);
        } else {
            responseDto.setStatus(InventoryStatus.AVAILABLE);
            responseDto.setMessage("All items are available and reserved successfully");
        }

        return responseDto;
    }

    @Transactional(readOnly = true)
    public List<BackorderResponseDto> getBackordersByOrderNumber(String orderNumber) {
        return inventoryMapper.toBackorderResponseList(
                backorderRepository.findByOrderNumber(orderNumber)
        );
    }

    @Transactional(readOnly = true)
    public List<BackorderResponseDto> getAllBackorders() {
        return inventoryMapper.toBackorderResponseList(backorderRepository.findAll());
    }

    private void createBackorder(String orderNumber, Inventory inventory, Integer requestQuantity) {
        Backorder backorder = new Backorder();
        backorder.setOrderNumber(orderNumber);
        backorder.setProductCode(inventory.getProductCode());
        backorder.setProductName(inventory.getProductName());
        backorder.setRequestedQuantity(requestQuantity);
        backorder.setMissingQuantity(requestQuantity - inventory.getAvailableQuantity());
        backorderRepository.save(backorder);
    }
}
