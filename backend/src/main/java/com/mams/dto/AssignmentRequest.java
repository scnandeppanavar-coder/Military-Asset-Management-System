package com.mams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public class AssignmentRequest {

    @NotNull(message = "Base ID is required")
    private Long baseId;

    @NotNull(message = "Equipment Type ID is required")
    private Long equipmentTypeId;

    @NotBlank(message = "Personnel name / unit is required")
    private String personnelName;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;

    @NotNull(message = "Assigned date is required")
    private LocalDate assignedDate;

    public AssignmentRequest() {}

    public AssignmentRequest(Long baseId, Long equipmentTypeId, String personnelName, Integer quantity, LocalDate assignedDate) {
        this.baseId = baseId;
        this.equipmentTypeId = equipmentTypeId;
        this.personnelName = personnelName;
        this.quantity = quantity;
        this.assignedDate = assignedDate;
    }

    public Long getBaseId() { return baseId; }
    public void setBaseId(Long baseId) { this.baseId = baseId; }

    public Long getEquipmentTypeId() { return equipmentTypeId; }
    public void setEquipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; }

    public String getPersonnelName() { return personnelName; }
    public void setPersonnelName(String personnelName) { this.personnelName = personnelName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDate assignedDate) { this.assignedDate = assignedDate; }

    public static AssignmentRequestBuilder builder() { return new AssignmentRequestBuilder(); }

    public static class AssignmentRequestBuilder {
        private Long baseId;
        private Long equipmentTypeId;
        private String personnelName;
        private Integer quantity;
        private LocalDate assignedDate;

        public AssignmentRequestBuilder baseId(Long baseId) { this.baseId = baseId; return this; }
        public AssignmentRequestBuilder equipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; return this; }
        public AssignmentRequestBuilder personnelName(String personnelName) { this.personnelName = personnelName; return this; }
        public AssignmentRequestBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public AssignmentRequestBuilder assignedDate(LocalDate assignedDate) { this.assignedDate = assignedDate; return this; }

        public AssignmentRequest build() {
            return new AssignmentRequest(baseId, equipmentTypeId, personnelName, quantity, assignedDate);
        }
    }
}
