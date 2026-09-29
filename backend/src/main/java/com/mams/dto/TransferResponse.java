package com.mams.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransferResponse {
    private Long id;
    private Long fromBaseId;
    private String fromBaseCode;
    private String fromBaseName;
    private Long toBaseId;
    private String toBaseCode;
    private String toBaseName;
    private Long equipmentTypeId;
    private String equipmentName;
    private String equipmentCategory;
    private String equipmentUnit;
    private Integer quantity;
    private LocalDate transferDate;
    private String referenceNumber;
    private String status;
    private String remarks;
    private String createdBy;
    private LocalDateTime createdAt;

    public TransferResponse() {}

    public TransferResponse(Long id, Long fromBaseId, String fromBaseCode, String fromBaseName, Long toBaseId, String toBaseCode, String toBaseName, Long equipmentTypeId, String equipmentName, String equipmentCategory, String equipmentUnit, Integer quantity, LocalDate transferDate, String referenceNumber, String status, String remarks, String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.fromBaseId = fromBaseId;
        this.fromBaseCode = fromBaseCode;
        this.fromBaseName = fromBaseName;
        this.toBaseId = toBaseId;
        this.toBaseCode = toBaseCode;
        this.toBaseName = toBaseName;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentName = equipmentName;
        this.equipmentCategory = equipmentCategory;
        this.equipmentUnit = equipmentUnit;
        this.quantity = quantity;
        this.transferDate = transferDate;
        this.referenceNumber = referenceNumber;
        this.status = status;
        this.remarks = remarks;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFromBaseId() { return fromBaseId; }
    public void setFromBaseId(Long fromBaseId) { this.fromBaseId = fromBaseId; }

    public String getFromBaseCode() { return fromBaseCode; }
    public void setFromBaseCode(String fromBaseCode) { this.fromBaseCode = fromBaseCode; }

    public String getFromBaseName() { return fromBaseName; }
    public void setFromBaseName(String fromBaseName) { this.fromBaseName = fromBaseName; }

    public Long getToBaseId() { return toBaseId; }
    public void setToBaseId(Long toBaseId) { this.toBaseId = toBaseId; }

    public String getToBaseCode() { return toBaseCode; }
    public void setToBaseCode(String toBaseCode) { this.toBaseCode = toBaseCode; }

    public String getToBaseName() { return toBaseName; }
    public void setToBaseName(String toBaseName) { this.toBaseName = toBaseName; }

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

    public static TransferResponseBuilder builder() { return new TransferResponseBuilder(); }

    public static class TransferResponseBuilder {
        private Long id;
        private Long fromBaseId;
        private String fromBaseCode;
        private String fromBaseName;
        private Long toBaseId;
        private String toBaseCode;
        private String toBaseName;
        private Long equipmentTypeId;
        private String equipmentName;
        private String equipmentCategory;
        private String equipmentUnit;
        private Integer quantity;
        private LocalDate transferDate;
        private String referenceNumber;
        private String status;
        private String remarks;
        private String createdBy;
        private LocalDateTime createdAt;

        public TransferResponseBuilder id(Long id) { this.id = id; return this; }
        public TransferResponseBuilder fromBaseId(Long fromBaseId) { this.fromBaseId = fromBaseId; return this; }
        public TransferResponseBuilder fromBaseCode(String fromBaseCode) { this.fromBaseCode = fromBaseCode; return this; }
        public TransferResponseBuilder fromBaseName(String fromBaseName) { this.fromBaseName = fromBaseName; return this; }
        public TransferResponseBuilder toBaseId(Long toBaseId) { this.toBaseId = toBaseId; return this; }
        public TransferResponseBuilder toBaseCode(String toBaseCode) { this.toBaseCode = toBaseCode; return this; }
        public TransferResponseBuilder toBaseName(String toBaseName) { this.toBaseName = toBaseName; return this; }
        public TransferResponseBuilder equipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; return this; }
        public TransferResponseBuilder equipmentName(String equipmentName) { this.equipmentName = equipmentName; return this; }
        public TransferResponseBuilder equipmentCategory(String equipmentCategory) { this.equipmentCategory = equipmentCategory; return this; }
        public TransferResponseBuilder equipmentUnit(String equipmentUnit) { this.equipmentUnit = equipmentUnit; return this; }
        public TransferResponseBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public TransferResponseBuilder transferDate(LocalDate transferDate) { this.transferDate = transferDate; return this; }
        public TransferResponseBuilder referenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; return this; }
        public TransferResponseBuilder status(String status) { this.status = status; return this; }
        public TransferResponseBuilder remarks(String remarks) { this.remarks = remarks; return this; }
        public TransferResponseBuilder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public TransferResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public TransferResponse build() {
            return new TransferResponse(id, fromBaseId, fromBaseCode, fromBaseName, toBaseId, toBaseCode, toBaseName, equipmentTypeId, equipmentName, equipmentCategory, equipmentUnit, quantity, transferDate, referenceNumber, status, remarks, createdBy, createdAt);
        }
    }
}
