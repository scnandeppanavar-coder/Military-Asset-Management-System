package com.mams.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_base_id", nullable = false)
    private Base fromBase;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_base_id", nullable = false)
    private Base toBase;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_type_id", nullable = false)
    private EquipmentType equipmentType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "transfer_date", nullable = false)
    private LocalDate transferDate;

    @Column(name = "reference_number", unique = true, nullable = false, length = 100)
    private String referenceNumber;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Transfer() {}

    public Transfer(Long id, Base fromBase, Base toBase, EquipmentType equipmentType, Integer quantity, LocalDate transferDate, String referenceNumber, String status, String remarks, String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.fromBase = fromBase;
        this.toBase = toBase;
        this.equipmentType = equipmentType;
        this.quantity = quantity;
        this.transferDate = transferDate;
        this.referenceNumber = referenceNumber;
        this.status = status;
        this.remarks = remarks;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "COMPLETED";
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Base getFromBase() { return fromBase; }
    public void setFromBase(Base fromBase) { this.fromBase = fromBase; }

    public Base getToBase() { return toBase; }
    public void setToBase(Base toBase) { this.toBase = toBase; }

    public EquipmentType getEquipmentType() { return equipmentType; }
    public void setEquipmentType(EquipmentType equipmentType) { this.equipmentType = equipmentType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getTransferDate() { return transferDate; }
    public void setTransferDate(LocalDate transferDate) { this.transferDate = transferDate; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static TransferBuilder builder() { return new TransferBuilder(); }

    public static class TransferBuilder {
        private Long id;
        private Base fromBase;
        private Base toBase;
        private EquipmentType equipmentType;
        private Integer quantity;
        private LocalDate transferDate;
        private String referenceNumber;
        private String status;
        private String remarks;
        private String createdBy;
        private LocalDateTime createdAt;

        public TransferBuilder id(Long id) { this.id = id; return this; }
        public TransferBuilder fromBase(Base fromBase) { this.fromBase = fromBase; return this; }
        public TransferBuilder toBase(Base toBase) { this.toBase = toBase; return this; }
        public TransferBuilder equipmentType(EquipmentType equipmentType) { this.equipmentType = equipmentType; return this; }
        public TransferBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public TransferBuilder transferDate(LocalDate transferDate) { this.transferDate = transferDate; return this; }
        public TransferBuilder referenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; return this; }
        public TransferBuilder status(String status) { this.status = status; return this; }
        public TransferBuilder remarks(String remarks) { this.remarks = remarks; return this; }
        public TransferBuilder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public TransferBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Transfer build() {
            return new Transfer(id, fromBase, toBase, equipmentType, quantity, transferDate, referenceNumber, status, remarks, createdBy, createdAt);
        }
    }
}
