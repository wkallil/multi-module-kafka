package wkallil.microservice.inventoryService.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import wkallil.microservice.inventoryService.dto.kafkaDto.InventoryResponseEventDto;
import wkallil.microservice.inventoryService.dto.kafkaDto.InventoryStatus;
import wkallil.microservice.inventoryService.dto.kafkaDto.OrderCreatedEventDto;
import wkallil.microservice.inventoryService.dto.kafkaDto.OrderItemEventDto;
import wkallil.microservice.inventoryService.dto.requestDto.CreateInventoryRequestDto;
import wkallil.microservice.inventoryService.dto.responseDto.InventoryResponseDto;
import wkallil.microservice.inventoryService.dto.responseDto.StockCheckResponseDto;
import wkallil.microservice.inventoryService.mapper.InventoryMapper;
import wkallil.microservice.inventoryService.model.Inventory;
import wkallil.microservice.inventoryService.repository.BackorderRepository;
import wkallil.microservice.inventoryService.repository.InventoryRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private BackorderRepository backorderRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inventory;
    private InventoryResponseDto inventoryResponse;
    private CreateInventoryRequestDto createInventoryRequest;

    @BeforeEach
    void setUp() {
        createInventoryRequest = new CreateInventoryRequestDto();
        createInventoryRequest.setProductCode("PROD-001");
        createInventoryRequest.setProductName("Test Product");
        createInventoryRequest.setAvailableQuantity(100);

        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setProductCode("PROD-001");
        inventory.setProductName("Test Product");
        inventory.setAvailableQuantity(100);
        inventory.setReservedQuantity(0);

        inventoryResponse = new InventoryResponseDto();
        inventoryResponse.setId(1L);
        inventoryResponse.setProductCode("PROD-001");
        inventoryResponse.setProductName("Test Product");
        inventoryResponse.setAvailableQuantity(100);
    }

    @Test
    void testCreateInventory_success() {
        when(inventoryRepository.existsByProductCode("PROD-001")).thenReturn(false);
        when(inventoryMapper.toEntity(createInventoryRequest)).thenReturn(inventory);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
        when(inventoryMapper.toResponse(inventory)).thenReturn(inventoryResponse);

        EntityModel<InventoryResponseDto> result = inventoryService.createInventory(createInventoryRequest);

        assertNotNull(result);
        assertEquals("PROD-001", result.getContent().getProductCode());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void testCreateInventory_ProductAlreadyExists() {
        // Given
        when(inventoryRepository.existsByProductCode("PROD-001")).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> inventoryService.createInventory(createInventoryRequest));
    }

    @Test
    void testGetInventoryByProductCode_Success() {
        // Given
        when(inventoryRepository.findByProductCode("PROD-001")).thenReturn(Optional.of(inventory));
        when(inventoryMapper.toResponse(inventory)).thenReturn(inventoryResponse);

        // When
        EntityModel<InventoryResponseDto> result = inventoryService.getInventoryByProductCode("PROD-001");

        // Then
        assertNotNull(result);
        assertEquals("PROD-001", result.getContent().getProductCode());
    }

    @Test
    void testGetInventoryByProductCode_NotFound() {
        // Given
        when(inventoryRepository.findByProductCode("PROD-999")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> inventoryService.getInventoryByProductCode("PROD-999"));
    }

    @Test
    void testCheckStock_Available() {
        // Given
        when(inventoryRepository.findByProductCode("PROD-001")).thenReturn(Optional.of(inventory));

        // When
        StockCheckResponseDto result = inventoryService.checkStock("PROD-001", 50);

        // Then
        assertTrue(result.isAvailable());
        assertEquals("Sufficient stock available", result.getMessage());
    }

    @Test
    void testCheckStock_InsufficientQuantity() {
        // Given
        when(inventoryRepository.findByProductCode("PROD-001")).thenReturn(Optional.of(inventory));

        // When
        StockCheckResponseDto result = inventoryService.checkStock("PROD-001", 150);

        // Then
        assertFalse(result.isAvailable());
        assertTrue(result.getMessage().contains("Insufficient stock"));
    }

    @Test
    void testCheckStock_ProductNotFound() {
        // Given
        when(inventoryRepository.findByProductCode("PROD-999")).thenReturn(Optional.empty());

        // When
        StockCheckResponseDto result = inventoryService.checkStock("PROD-999", 10);

        // Then
        assertFalse(result.isAvailable());
        assertEquals("Product does not exist in inventory", result.getMessage());
    }

    @Test
    void testProcessOrderRequest_AllItemsAvailable() {
        // Given
        OrderItemEventDto itemEvent = new OrderItemEventDto();
        itemEvent.setProductCode("PROD-001");
        itemEvent.setQuantity(50);

        OrderCreatedEventDto orderEvent = new OrderCreatedEventDto();
        orderEvent.setOrderNumber("ORD-12345678");
        orderEvent.setItems(Arrays.asList(itemEvent));

        when(inventoryRepository.findByProductCode("PROD-001")).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        // When
        InventoryResponseEventDto result = inventoryService.processOrderRequest(orderEvent);

        // Then
        assertEquals(InventoryStatus.AVAILABLE, result.getStatus());
        assertEquals("ORD-12345678", result.getOrderNumber());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void testProcessOrderRequest_ItemNotFound() {
        // Given
        OrderItemEventDto itemEvent = new OrderItemEventDto();
        itemEvent.setProductCode("PROD-999");
        itemEvent.setQuantity(10);

        OrderCreatedEventDto orderEvent = new OrderCreatedEventDto();
        orderEvent.setOrderNumber("ORD-12345678");
        orderEvent.setItems(Arrays.asList(itemEvent));

        when(inventoryRepository.findByProductCode("PROD-999")).thenReturn(Optional.empty());

        // When
        InventoryResponseEventDto result = inventoryService.processOrderRequest(orderEvent);

        // Then
        assertEquals(InventoryStatus.UNAVAILABLE, result.getStatus());
        assertTrue(result.getMessage().contains("does not exist"));
    }

    @Test
    void testProcessOrderRequest_InsufficientQuantity() {
        // Given
        OrderItemEventDto itemEvent = new OrderItemEventDto();
        itemEvent.setProductCode("PROD-001");
        itemEvent.setQuantity(150);

        OrderCreatedEventDto orderEvent = new OrderCreatedEventDto();
        orderEvent.setOrderNumber("ORD-12345678");
        orderEvent.setItems(Arrays.asList(itemEvent));

        when(inventoryRepository.findByProductCode("PROD-001")).thenReturn(Optional.of(inventory));

        // When
        InventoryResponseEventDto result = inventoryService.processOrderRequest(orderEvent);

        // Then
        assertEquals(InventoryStatus.PARTIALLY_AVAILABLE, result.getStatus());
        assertTrue(result.getMessage().contains("insufficient stock"));
        verify(backorderRepository, times(1)).save(any());
    }
}
