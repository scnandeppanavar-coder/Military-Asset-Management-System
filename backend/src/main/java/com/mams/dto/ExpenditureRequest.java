package com.mams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class ExpenditureRequest {

    @NotNull(message = "Base ID is required")
    private Long baseId;

    @NotNull(message = "Equipment Type ID is required")
    private Long equipmentTypeId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;

    @NotNull(message = "Expenditure date is required")
    private LocalDate expenditureDate;

    @NotBlank(message = "Reason for expenditure is required")
    private String reason;

    @Size(max = 100, message = "Reference number cannot exceed 100 characters")
    private String referenceNumber;

    public ExpenditureRequest() {}

    public ExpenditureRequest(Long baseId, Long equipmentTypeId, Integer quantity, LocalDate expenditureDate, String reason, String referenceNumber) {
        this.baseId = baseId;
        this.equipmentTypeId = equipmentTypeId;
        this.quantity = quantity;
        this.expenditureDate = expenditureDate;
        this.reason = reason;
        this.referenceNumber = referenceNumber;
    }

    public Long getBaseId() { return baseId; }
    public void setBaseId(Long baseId) { this.baseId = baseId; }

    public Long getEquipmentTypeId() { return equipmentTypeId; }
    public void setEquipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getExpenditureDate() { return expenditureDate; }
    public void setExpenditureDate(LocalDate expenditureDate) { this.expenditureDate = expenditureDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public static ExpenditureRequestBuilder builder() { return new ExpenditureRequestBuilder(); }

    public static class ExpenditureRequestBuilder {
        private Long baseId;
        private Long equipmentTypeId;
        private Integer quantity;
        private LocalDate expenditureDate;
        private String reason;
        private String referenceNumber;

        public ExpenditureRequestBuilder baseId(Long baseId) { this.baseId = baseId; return this; }
        public ExpenditureRequestBuilder equipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; return this; }
        public ExpenditureRequestBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public ExpenditureRequestBuilder expenditureDate(LocalDate expenditureDate) { this.expenditureDate = expenditureDate; return this; }
        public ExpenditureRequestBuilder reason(String reason) { this.reason = reason; return this; }
        public ExpenditureRequestBuilder referenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; return this; }

        public ExpenditureRequest build() {
            return new ExpenditureRequest(baseId, equipmentTypeId, quantity, expenditureDate, reason, referenceNumber);
        }
    }
}
