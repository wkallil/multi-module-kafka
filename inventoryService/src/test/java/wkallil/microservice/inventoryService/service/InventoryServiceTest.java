package wkallil.microservice.inventoryService.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wkallil.microservice.inventoryService.dto.requestDto.CreateInventoryRequestDto;
import wkallil.microservice.inventoryService.dto.responseDto.InventoryResponseDto;
import wkallil.microservice.inventoryService.mapper.InventoryMapper;
import wkallil.microservice.inventoryService.model.Inventory;
import wkallil.microservice.inventoryService.repository.BackorderRepository;
import wkallil.microservice.inventoryService.repository.InventoryRepository;

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

}
