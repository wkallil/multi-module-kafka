package wkallil.microservice.inventoryService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import wkallil.microservice.inventoryService.dto.requestDto.CreateInventoryRequestDto;
import wkallil.microservice.inventoryService.dto.requestDto.UpdateInventoryRequestDto;
import wkallil.microservice.inventoryService.dto.responseDto.BackorderResponseDto;
import wkallil.microservice.inventoryService.dto.responseDto.InventoryResponseDto;
import wkallil.microservice.inventoryService.model.Backorder;
import wkallil.microservice.inventoryService.model.Inventory;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reservedQuantity", constant = "0")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Inventory toEntity(CreateInventoryRequestDto requestDto);

    @Mapping(target = "totalQuantity", expression = "java(inventory.getTotalQuantity())")
    InventoryResponseDto toResponse(Inventory inventory);

    List<InventoryResponseDto> toResponseList(List<Inventory> inventories);

    BackorderResponseDto toResponse(Backorder backOrder);

    List<BackorderResponseDto> toBackorderResponseList(List<Backorder> backorders);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateInventoryFromRequest(UpdateInventoryRequestDto request, @MappingTarget Inventory inventory);
}
