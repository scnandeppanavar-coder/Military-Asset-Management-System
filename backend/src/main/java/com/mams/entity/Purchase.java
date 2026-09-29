package com.mams.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_type_id", nullable = false)
    private EquipmentType equipmentType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "reference_number", unique = true, nullable = false, length = 100)
    private String referenceNumber;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Purchase() {}

    public Purchase(Long id, Base base, EquipmentType equipmentType, Integer quantity, LocalDate purchaseDate, String referenceNumber, String remarks, String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.base = base;
        this.equipmentType = equipmentType;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
        this.referenceNumber = referenceNumber;
        this.remarks = remarks;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Base getBase() { return base; }
    public void setBase(Base base) { this.base = base; }

    public EquipmentType getEquipmentType() { return equipmentType; }
    public void setEquipmentType(EquipmentType equipmentType) { this.equipmentType = equipmentType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static PurchaseBuilder builder() { return new PurchaseBuilder(); }

    public static class PurchaseBuilder {
        private Long id;
        private Base base;
        private EquipmentType equipmentType;
        private Integer quantity;
        private LocalDate purchaseDate;
        private String referenceNumber;
        private String remarks;
        private String createdBy;
        private LocalDateTime createdAt;

        public PurchaseBuilder id(Long id) { this.id = id; return this; }
        public PurchaseBuilder base(Base base) { this.base = base; return this; }
        public PurchaseBuilder equipmentType(EquipmentType equipmentType) { this.equipmentType = equipmentType; return this; }
        public PurchaseBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public PurchaseBuilder purchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; return this; }
        public PurchaseBuilder referenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; return this; }
        public PurchaseBuilder remarks(String remarks) { this.remarks = remarks; return this; }
        public PurchaseBuilder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public PurchaseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Purchase build() {
            return new Purchase(id, base, equipmentType, quantity, purchaseDate, referenceNumber, remarks, createdBy, createdAt);
        }
    }
}
