package wkallil.microservice.inventoryService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wkallil.microservice.inventoryService.model.Backorder;

import java.util.List;

public interface BackorderRepository extends JpaRepository<Backorder, Long> {

    List<Backorder> findByOrderNumber(String orderNumber);

    List<Backorder> findByProductCode(String productCode);

    @Query("SELECT b FROM Backorder b WHERE b.status = 'PENDING' ORDER BY b.createdAt ASC")
    List<Backorder> findPendingBackorders();
}
