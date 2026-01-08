package wkallil.microservice.orderService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wkallil.microservice.orderService.model.Order;
import wkallil.microservice.orderService.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByCustomerName(String customerName);

    boolean existsByOrderNumber(String orderNumber);
}
