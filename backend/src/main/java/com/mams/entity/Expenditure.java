package com.mams.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenditures")
public class Expenditure {

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

    @Column(name = "expenditure_date", nullable = false)
    private LocalDate expenditureDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "reference_number", unique = true, nullable = false, length = 100)
    private String referenceNumber;

    @Column(name = "recorded_by", nullable = false, length = 50)
    private String recordedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Expenditure() {}

    public Expenditure(Long id, Base base, EquipmentType equipmentType, Integer quantity, LocalDate expenditureDate, String reason, String referenceNumber, String recordedBy, LocalDateTime createdAt) {
        this.id = id;
        this.base = base;
        this.equipmentType = equipmentType;
        this.quantity = quantity;
        this.expenditureDate = expenditureDate;
        this.reason = reason;
        this.referenceNumber = referenceNumber;
        this.recordedBy = recordedBy;
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

    public LocalDate getExpenditureDate() { return expenditureDate; }
    public void setExpenditureDate(LocalDate expenditureDate) { this.expenditureDate = expenditureDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static ExpenditureBuilder builder() { return new ExpenditureBuilder(); }

    public static class ExpenditureBuilder {
        private Long id;
        private Base base;
        private EquipmentType equipmentType;
        private Integer quantity;
        private LocalDate expenditureDate;
        private String reason;
        private String referenceNumber;
        private String recordedBy;
        private LocalDateTime createdAt;

        public ExpenditureBuilder id(Long id) { this.id = id; return this; }
        public ExpenditureBuilder base(Base base) { this.base = base; return this; }
        public ExpenditureBuilder equipmentType(EquipmentType equipmentType) { this.equipmentType = equipmentType; return this; }
        public ExpenditureBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public ExpenditureBuilder expenditureDate(LocalDate expenditureDate) { this.expenditureDate = expenditureDate; return this; }
        public ExpenditureBuilder reason(String reason) { this.reason = reason; return this; }
        public ExpenditureBuilder referenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; return this; }
        public ExpenditureBuilder recordedBy(String recordedBy) { this.recordedBy = recordedBy; return this; }
        public ExpenditureBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Expenditure build() {
            return new Expenditure(id, base, equipmentType, quantity, expenditureDate, reason, referenceNumber, recordedBy, createdAt);
        }
    }
}
