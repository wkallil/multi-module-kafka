package wkallil.microservice.inventoryService.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "backorders")
public class Backorder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private String productCode;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer requestedQuantity;

    @Column(nullable = false)
    private Integer missingQuantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BackOrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Backorder() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(Integer requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public Integer getMissingQuantity() {
        return missingQuantity;
    }

    public void setMissingQuantity(Integer missingQuantity) {
        this.missingQuantity = missingQuantity;
    }

    public BackOrderStatus getStatus() {
        return status;
    }

    public void setStatus(BackOrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Backorder backOrder = (Backorder) o;
        return Objects.equals(id, backOrder.id) && Objects.equals(orderNumber, backOrder.orderNumber) && Objects.equals(productCode, backOrder.productCode) && Objects.equals(productName, backOrder.productName) && Objects.equals(requestedQuantity, backOrder.requestedQuantity) && Objects.equals(missingQuantity, backOrder.missingQuantity) && status == backOrder.status && Objects.equals(createdAt, backOrder.createdAt) && Objects.equals(updatedAt, backOrder.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderNumber, productCode, productName, requestedQuantity, missingQuantity, status, createdAt, updatedAt);
    }
}
