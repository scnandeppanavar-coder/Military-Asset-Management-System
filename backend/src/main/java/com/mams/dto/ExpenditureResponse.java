package com.mams.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ExpenditureResponse {
    private Long id;
    private Long baseId;
    private String baseCode;
    private String baseName;
    private Long equipmentTypeId;
    private String equipmentName;
    private String equipmentCategory;
    private String equipmentUnit;
    private Integer quantity;
    private LocalDate expenditureDate;
    private String reason;
    private String referenceNumber;
    private String recordedBy;
    private LocalDateTime createdAt;

    public ExpenditureResponse() {}

    public ExpenditureResponse(Long id, Long baseId, String baseCode, String baseName, Long equipmentTypeId, String equipmentName, String equipmentCategory, String equipmentUnit, Integer quantity, LocalDate expenditureDate, String reason, String referenceNumber, String recordedBy, LocalDateTime createdAt) {
        this.id = id;
        this.baseId = baseId;
        this.baseCode = baseCode;
        this.baseName = baseName;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentName = equipmentName;
        this.equipmentCategory = equipmentCategory;
        this.equipmentUnit = equipmentUnit;
        this.quantity = quantity;
        this.expenditureDate = expenditureDate;
        this.reason = reason;
        this.referenceNumber = referenceNumber;
        this.recordedBy = recordedBy;
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

    public static ExpenditureResponseBuilder builder() { return new ExpenditureResponseBuilder(); }

    public static class ExpenditureResponseBuilder {
        private Long id;
        private Long baseId;
        private String baseCode;
        private String baseName;
        private Long equipmentTypeId;
        private String equipmentName;
        private String equipmentCategory;
        private String equipmentUnit;
        private Integer quantity;
        private LocalDate expenditureDate;
        private String reason;
        private String referenceNumber;
        private String recordedBy;
        private LocalDateTime createdAt;

        public ExpenditureResponseBuilder id(Long id) { this.id = id; return this; }
        public ExpenditureResponseBuilder baseId(Long baseId) { this.baseId = baseId; return this; }
        public ExpenditureResponseBuilder baseCode(String baseCode) { this.baseCode = baseCode; return this; }
        public ExpenditureResponseBuilder baseName(String baseName) { this.baseName = baseName; return this; }
        public ExpenditureResponseBuilder equipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; return this; }
        public ExpenditureResponseBuilder equipmentName(String equipmentName) { this.equipmentName = equipmentName; return this; }
        public ExpenditureResponseBuilder equipmentCategory(String equipmentCategory) { this.equipmentCategory = equipmentCategory; return this; }
        public ExpenditureResponseBuilder equipmentUnit(String equipmentUnit) { this.equipmentUnit = equipmentUnit; return this; }
        public ExpenditureResponseBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public ExpenditureResponseBuilder expenditureDate(LocalDate expenditureDate) { this.expenditureDate = expenditureDate; return this; }
        public ExpenditureResponseBuilder reason(String reason) { this.reason = reason; return this; }
        public ExpenditureResponseBuilder referenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; return this; }
        public ExpenditureResponseBuilder recordedBy(String recordedBy) { this.recordedBy = recordedBy; return this; }
        public ExpenditureResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ExpenditureResponse build() {
            return new ExpenditureResponse(id, baseId, baseCode, baseName, equipmentTypeId, equipmentName, equipmentCategory, equipmentUnit, quantity, expenditureDate, reason, referenceNumber, recordedBy, createdAt);
        }
    }
}
