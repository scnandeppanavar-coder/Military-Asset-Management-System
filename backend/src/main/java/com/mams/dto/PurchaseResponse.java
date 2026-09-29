package com.mams.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PurchaseResponse {
    private Long id;
    private Long baseId;
    private String baseCode;
    private String baseName;
    private Long equipmentTypeId;
    private String equipmentName;
    private String equipmentCategory;
    private String equipmentUnit;
    private Integer quantity;
    private LocalDate purchaseDate;
    private String referenceNumber;
    private String remarks;
    private String createdBy;
    private LocalDateTime createdAt;

    public PurchaseResponse() {}

    public PurchaseResponse(Long id, Long baseId, String baseCode, String baseName, Long equipmentTypeId, String equipmentName, String equipmentCategory, String equipmentUnit, Integer quantity, LocalDate purchaseDate, String referenceNumber, String remarks, String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.baseId = baseId;
        this.baseCode = baseCode;
        this.baseName = baseName;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentName = equipmentName;
        this.equipmentCategory = equipmentCategory;
        this.equipmentUnit = equipmentUnit;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
        this.referenceNumber = referenceNumber;
        this.remarks = remarks;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBaseId() { return baseId; }
    public void setBaseId(Long baseId) { this.baseId = baseId; }

    public String getBaseCode() { return baseCode; }
    public void setBaseCode(String baseCode) { this.baseCode = baseCode; }

    public String getBaseName() { return baseName; }
    public void setBaseName(String baseName) { this.baseName = baseName; }

    public Long getEquipmentTypeId() { return equipmentTypeId; }
    public void setEquipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; }

    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }

    public String getEquipmentCategory() { return equipmentCategory; }
    public void setEquipmentCategory(String equipmentCategory) { this.equipmentCategory = equipmentCategory; }

    public String getEquipmentUnit() { return equipmentUnit; }
    public void setEquipmentUnit(String equipmentUnit) { this.equipmentUnit = equipmentUnit; }

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

    public static PurchaseResponseBuilder builder() { return new PurchaseResponseBuilder(); }

    public static class PurchaseResponseBuilder {
        private Long id;
        private Long baseId;
        private String baseCode;
        private String baseName;
        private Long equipmentTypeId;
        private String equipmentName;
        private String equipmentCategory;
        private String equipmentUnit;
        private Integer quantity;
        private LocalDate purchaseDate;
        private String referenceNumber;
        private String remarks;
        private String createdBy;
        private LocalDateTime createdAt;

        public PurchaseResponseBuilder id(Long id) { this.id = id; return this; }
        public PurchaseResponseBuilder baseId(Long baseId) { this.baseId = baseId; return this; }
        public PurchaseResponseBuilder baseCode(String baseCode) { this.baseCode = baseCode; return this; }
        public PurchaseResponseBuilder baseName(String baseName) { this.baseName = baseName; return this; }
        public PurchaseResponseBuilder equipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; return this; }
        public PurchaseResponseBuilder equipmentName(String equipmentName) { this.equipmentName = equipmentName; return this; }
        public PurchaseResponseBuilder equipmentCategory(String equipmentCategory) { this.equipmentCategory = equipmentCategory; return this; }
        public PurchaseResponseBuilder equipmentUnit(String equipmentUnit) { this.equipmentUnit = equipmentUnit; return this; }
        public PurchaseResponseBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public PurchaseResponseBuilder purchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; return this; }
        public PurchaseResponseBuilder referenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; return this; }
        public PurchaseResponseBuilder remarks(String remarks) { this.remarks = remarks; return this; }
        public PurchaseResponseBuilder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public PurchaseResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public PurchaseResponse build() {
            return new PurchaseResponse(id, baseId, baseCode, baseName, equipmentTypeId, equipmentName, equipmentCategory, equipmentUnit, quantity, purchaseDate, referenceNumber, remarks, createdBy, createdAt);
        }
    }
}
