package com.mams.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_movements")
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_type_id", nullable = false)
    private EquipmentType equipmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 30)
    private MovementType movementType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "movement_date", nullable = false)
    private LocalDate movementDate;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public InventoryMovement() {}

    public InventoryMovement(Long id, Base base, EquipmentType equipmentType, MovementType movementType, Integer quantity, Long referenceId, LocalDate movementDate, String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.base = base;
        this.equipmentType = equipmentType;
        this.movementType = movementType;
        this.quantity = quantity;
        this.referenceId = referenceId;
        this.movementDate = movementDate;
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

    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public LocalDate getMovementDate() { return movementDate; }
    public void setMovementDate(LocalDate movementDate) { this.movementDate = movementDate; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static InventoryMovementBuilder builder() { return new InventoryMovementBuilder(); }

    public static class InventoryMovementBuilder {
        private Long id;
        private Base base;
        private EquipmentType equipmentType;
        private MovementType movementType;
        private Integer quantity;
        private Long referenceId;
        private LocalDate movementDate;
        private String createdBy;
        private LocalDateTime createdAt;

        public InventoryMovementBuilder id(Long id) { this.id = id; return this; }
        public InventoryMovementBuilder base(Base base) { this.base = base; return this; }
        public InventoryMovementBuilder equipmentType(EquipmentType equipmentType) { this.equipmentType = equipmentType; return this; }
        public InventoryMovementBuilder movementType(MovementType movementType) { this.movementType = movementType; return this; }
        public InventoryMovementBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public InventoryMovementBuilder referenceId(Long referenceId) { this.referenceId = referenceId; return this; }
        public InventoryMovementBuilder movementDate(LocalDate movementDate) { this.movementDate = movementDate; return this; }
        public InventoryMovementBuilder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public InventoryMovementBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public InventoryMovement build() {
            return new InventoryMovement(id, base, equipmentType, movementType, quantity, referenceId, movementDate, createdBy, createdAt);
        }
    }
}
