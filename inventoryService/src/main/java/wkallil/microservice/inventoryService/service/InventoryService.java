package wkallil.microservice.inventoryService.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wkallil.microservice.inventoryService.controller.InventoryController;
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
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    public EntityModel<InventoryResponseDto> createInventory(CreateInventoryRequestDto requestDto) {
        if (inventoryRepository.existsByProductCode(requestDto.getProductCode())) {
            throw new RuntimeException("Product already exists " + requestDto.getProductCode());
        }

        Inventory inventory = inventoryMapper.toEntity(requestDto);
        Inventory savedInventory = inventoryRepository.save(inventory);
        InventoryResponseDto response = inventoryMapper.toResponse(savedInventory);

        return addHateoasLinks(response);
    }

    @Transactional(readOnly = true)
    public EntityModel<InventoryResponseDto> getInventoryByProductCode(String productCode) {
        Inventory inventory = inventoryRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productCode));
        InventoryResponseDto response = inventoryMapper.toResponse(inventory);

        return addHateoasLinks(response);
    }

    @Transactional(readOnly = true)
    public PagedModel<EntityModel<InventoryResponseDto>> getAllInventories(Pageable pageable) {
        Page<Inventory> inventoryPage = inventoryRepository.findAll(pageable);

        List<EntityModel<InventoryResponseDto>> inventories = inventoryPage.getContent().stream()
                .map(inventory -> {
                    InventoryResponseDto response = inventoryMapper.toResponse(inventory);
                    return addHateoasLinks(response);
                }).collect(Collectors.toList());

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                inventoryPage.getSize(),
                inventoryPage.getNumber(),
                inventoryPage.getTotalElements(),
                inventoryPage.getTotalPages()
        );

        PagedModel<EntityModel<InventoryResponseDto>> pagedModel = PagedModel.of(inventories, metadata);

        pagedModel.add(linkTo(methodOn(InventoryController.class)
                .getAllInventories(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()))
                .withSelfRel());

        if (inventoryPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(InventoryController.class)
                    .getAllInventories(pageable.getPageNumber() + 1, pageable.getPageSize(), pageable.getSort()))
                    .withRel("next"));
        }

        if (inventoryPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(InventoryController.class)
                    .getAllInventories(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort()))
                    .withRel("prev"));
        }

        pagedModel.add(linkTo(methodOn(InventoryController.class)
                .getAllInventories(0, pageable.getPageSize(), pageable.getSort()))
                .withRel("first"));

        pagedModel.add(linkTo(methodOn(InventoryController.class)
                .getAllInventories(inventoryPage.getTotalPages() - 1, pageable.getPageSize(), pageable.getSort()))
                .withRel("last"));

        return pagedModel;
    }

    @Transactional
    public EntityModel<InventoryResponseDto> updateInventory(String productCode, UpdateInventoryRequestDto requestDto) {
        Inventory inventory = inventoryRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productCode));

        inventoryMapper.updateInventoryFromRequest(requestDto, inventory);
        Inventory updatedInventory = inventoryRepository.save(inventory);
        InventoryResponseDto response = inventoryMapper.toResponse(updatedInventory);

        return addHateoasLinks(response);
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
    public PagedModel<EntityModel<BackorderResponseDto>> getBackordersByOrderNumber(String orderNumber, Pageable pageable) {
        Page<Backorder> backorderPage = backorderRepository.findByOrderNumber(orderNumber, pageable);

        return createBackorderPagedModel(backorderPage, pageable);
    }

    @Transactional(readOnly = true)
    public PagedModel<EntityModel<BackorderResponseDto>> getAllBackorders(Pageable pageable) {
        Page<Backorder> backorderPage = backorderRepository.findAll(pageable);

        return createBackorderPagedModel(backorderPage, pageable);
    }

    private EntityModel<InventoryResponseDto> addHateoasLinks(InventoryResponseDto response) {
        EntityModel<InventoryResponseDto> entityModel = EntityModel.of(response);

        entityModel.add(linkTo(methodOn(InventoryController.class)
                .getInventoryByProductCode(response.getProductCode()))
                .withSelfRel());

        entityModel.add(linkTo(methodOn(InventoryController.class)
                .checkStock(response.getProductCode(), 1))
                .withRel("check-stock"));

        entityModel.add(linkTo(methodOn(InventoryController.class)
                .updateInventory(response.getProductCode(), null))
                .withRel("update"));

        entityModel.add(linkTo(methodOn(InventoryController.class)
                .getAllInventories(0, 5, null))
                .withRel("all-inventories"));

        return entityModel;
    }

    private EntityModel<BackorderResponseDto> addBackorderHateoasLinks(BackorderResponseDto response) {
        EntityModel<BackorderResponseDto> entityModel = EntityModel.of(response);

        entityModel.add(linkTo(methodOn(InventoryController.class)
                .getInventoryByProductCode(response.getProductCode()))
                .withRel("product"));

        entityModel.add(linkTo(methodOn(InventoryController.class)
                .getBackordersByOrderNumber(response.getOrderNumber(), 0, 5, null))
                .withRel("order-backorders"));

        entityModel.add(linkTo(methodOn(InventoryController.class)
                .getAllBackorders(0, 5, null))
                .withRel("all-backorders"));

        return entityModel;
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

    private PagedModel<EntityModel<BackorderResponseDto>> createBackorderPagedModel(Page<Backorder> backorderPage, Pageable pageable) {
        List<EntityModel<BackorderResponseDto>> backorders = backorderPage.getContent().stream()
                .map(backorder -> {
                    BackorderResponseDto response = inventoryMapper.toResponse(backorder);
                    return addBackorderHateoasLinks(response);
                }).collect(Collectors.toList());

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                backorderPage.getSize(),
                backorderPage.getNumber(),
                backorderPage.getTotalElements(),
                backorderPage.getTotalPages()
        );

        PagedModel<EntityModel<BackorderResponseDto>> pagedModel = PagedModel.of(backorders, metadata);

        pagedModel.add(linkTo(methodOn(InventoryController.class)
                .getAllBackorders(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()))
                .withSelfRel());

        if (backorderPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(InventoryController.class)
                    .getAllBackorders(pageable.getPageNumber() + 1, pageable.getPageSize(), pageable.getSort()))
                    .withRel("next"));
        }

        if (backorderPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(InventoryController.class)
                    .getAllBackorders(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort()))
                    .withRel("prev"));
        }

        return pagedModel;
    }
}
