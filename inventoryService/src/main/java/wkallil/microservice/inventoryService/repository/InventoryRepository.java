package wkallil.microservice.inventoryService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wkallil.microservice.inventoryService.model.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductCode(String productCode);

    boolean existsByProductCode(String productCode);

    @Query("SELECT i FROM Inventory i WHERE i.availableQuantity < :threshold")
    List<Inventory> findLowStockItems(Integer threshold);

    @Query("SELECT i FROM Inventory i WHERE i.availableQuantity = 0")
    List<Inventory> findOutOfStockItems();
}
