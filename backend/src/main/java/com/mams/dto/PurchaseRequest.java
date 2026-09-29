package com.mams.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class PurchaseRequest {

    @NotNull(message = "Base ID is required")
    private Long baseId;

    @NotNull(message = "Equipment Type ID is required")
    private Long equipmentTypeId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;

    @NotNull(message = "Purchase date is required")
    private LocalDate purchaseDate;

    @Size(max = 100, message = "Reference number cannot exceed 100 characters")
    private String referenceNumber;

    private String remarks;

    public PurchaseRequest() {}

    public PurchaseRequest(Long baseId, Long equipmentTypeId, Integer quantity, LocalDate purchaseDate, String referenceNumber, String remarks) {
        this.baseId = baseId;
        this.equipmentTypeId = equipmentTypeId;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
        this.referenceNumber = referenceNumber;
        this.remarks = remarks;
    }

    public Long getBaseId() { return baseId; }
    public void setBaseId(Long baseId) { this.baseId = baseId; }

    public Long getEquipmentTypeId() { return equipmentTypeId; }
    public void setEquipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public static PurchaseRequestBuilder builder() { return new PurchaseRequestBuilder(); }

    public static class PurchaseRequestBuilder {
        private Long baseId;
        private Long equipmentTypeId;
        private Integer quantity;
        private LocalDate purchaseDate;
        private String referenceNumber;
        private String remarks;

        public PurchaseRequestBuilder baseId(Long baseId) { this.baseId = baseId; return this; }
        public PurchaseRequestBuilder equipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; return this; }
        public PurchaseRequestBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public PurchaseRequestBuilder purchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; return this; }
        public PurchaseRequestBuilder referenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; return this; }
        public PurchaseRequestBuilder remarks(String remarks) { this.remarks = remarks; return this; }

        public PurchaseRequest build() {
            return new PurchaseRequest(baseId, equipmentTypeId, quantity, purchaseDate, referenceNumber, remarks);
        }
    }
}
