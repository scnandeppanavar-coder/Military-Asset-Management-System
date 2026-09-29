package com.mams.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class TransferRequest {

    @NotNull(message = "Source base (fromBaseId) is required")
    private Long fromBaseId;

    @NotNull(message = "Destination base (toBaseId) is required")
    private Long toBaseId;

    @NotNull(message = "Equipment Type ID is required")
    private Long equipmentTypeId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;

    @NotNull(message = "Transfer date is required")
    private LocalDate transferDate;

    @Size(max = 100, message = "Reference number cannot exceed 100 characters")
    private String referenceNumber;

    private String remarks;

    public TransferRequest() {}

    public TransferRequest(Long fromBaseId, Long toBaseId, Long equipmentTypeId, Integer quantity, LocalDate transferDate, String referenceNumber, String remarks) {
        this.fromBaseId = fromBaseId;
        this.toBaseId = toBaseId;
        this.equipmentTypeId = equipmentTypeId;
        this.quantity = quantity;
        this.transferDate = transferDate;
        this.referenceNumber = referenceNumber;
        this.remarks = remarks;
    }

    public Long getFromBaseId() { return fromBaseId; }
    public void setFromBaseId(Long fromBaseId) { this.fromBaseId = fromBaseId; }

    public Long getToBaseId() { return toBaseId; }
    public void setToBaseId(Long toBaseId) { this.toBaseId = toBaseId; }

    public Long getEquipmentTypeId() { return equipmentTypeId; }
    public void setEquipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getTransferDate() { return transferDate; }
    public void setTransferDate(LocalDate transferDate) { this.transferDate = transferDate; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public static TransferRequestBuilder builder() { return new TransferRequestBuilder(); }

    public static class TransferRequestBuilder {
        private Long fromBaseId;
        private Long toBaseId;
        private Long equipmentTypeId;
        private Integer quantity;
        private LocalDate transferDate;
        private String referenceNumber;
        private String remarks;

        public TransferRequestBuilder fromBaseId(Long fromBaseId) { this.fromBaseId = fromBaseId; return this; }
        public TransferRequestBuilder toBaseId(Long toBaseId) { this.toBaseId = toBaseId; return this; }
        public TransferRequestBuilder equipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; return this; }
        public TransferRequestBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public TransferRequestBuilder transferDate(LocalDate transferDate) { this.transferDate = transferDate; return this; }
        public TransferRequestBuilder referenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; return this; }
        public TransferRequestBuilder remarks(String remarks) { this.remarks = remarks; return this; }

        public TransferRequest build() {
            return new TransferRequest(fromBaseId, toBaseId, equipmentTypeId, quantity, transferDate, referenceNumber, remarks);
        }
    }
}
