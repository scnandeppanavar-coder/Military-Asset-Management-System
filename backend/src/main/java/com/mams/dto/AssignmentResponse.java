package com.mams.dto;

import com.mams.entity.AssignmentStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AssignmentResponse {
    private Long id;
    private Long baseId;
    private String baseCode;
    private String baseName;
    private Long equipmentTypeId;
    private String equipmentName;
    private String equipmentCategory;
    private String equipmentUnit;
    private String personnelName;
    private Integer quantity;
    private Integer returnedQuantity;
    private Integer outstandingQuantity;
    private LocalDate assignedDate;
    private AssignmentStatus status;
    private String assignedBy;
    private LocalDateTime createdAt;

    public AssignmentResponse() {}

    public AssignmentResponse(Long id, Long baseId, String baseCode, String baseName, Long equipmentTypeId, String equipmentName, String equipmentCategory, String equipmentUnit, String personnelName, Integer quantity, Integer returnedQuantity, Integer outstandingQuantity, LocalDate assignedDate, AssignmentStatus status, String assignedBy, LocalDateTime createdAt) {
        this.id = id;
        this.baseId = baseId;
        this.baseCode = baseCode;
        this.baseName = baseName;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentName = equipmentName;
        this.equipmentCategory = equipmentCategory;
        this.equipmentUnit = equipmentUnit;
        this.personnelName = personnelName;
        this.quantity = quantity;
        this.returnedQuantity = returnedQuantity;
        this.outstandingQuantity = outstandingQuantity;
        this.assignedDate = assignedDate;
        this.status = status;
        this.assignedBy = assignedBy;
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

    public String getPersonnelName() { return personnelName; }
    public void setPersonnelName(String personnelName) { this.personnelName = personnelName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getReturnedQuantity() { return returnedQuantity; }
    public void setReturnedQuantity(Integer returnedQuantity) { this.returnedQuantity = returnedQuantity; }

    public Integer getOutstandingQuantity() { return outstandingQuantity; }
    public void setOutstandingQuantity(Integer outstandingQuantity) { this.outstandingQuantity = outstandingQuantity; }

    public LocalDate getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDate assignedDate) { this.assignedDate = assignedDate; }

    public AssignmentStatus getStatus() { return status; }
    public void setStatus(AssignmentStatus status) { this.status = status; }

    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static AssignmentResponseBuilder builder() { return new AssignmentResponseBuilder(); }

    public static class AssignmentResponseBuilder {
        private Long id;
        private Long baseId;
        private String baseCode;
        private String baseName;
        private Long equipmentTypeId;
        private String equipmentName;
        private String equipmentCategory;
        private String equipmentUnit;
        private String personnelName;
        private Integer quantity;
        private Integer returnedQuantity;
        private Integer outstandingQuantity;
        private LocalDate assignedDate;
        private AssignmentStatus status;
        private String assignedBy;
        private LocalDateTime createdAt;

        public AssignmentResponseBuilder id(Long id) { this.id = id; return this; }
        public AssignmentResponseBuilder baseId(Long baseId) { this.baseId = baseId; return this; }
        public AssignmentResponseBuilder baseCode(String baseCode) { this.baseCode = baseCode; return this; }
        public AssignmentResponseBuilder baseName(String baseName) { this.baseName = baseName; return this; }
        public AssignmentResponseBuilder equipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; return this; }
        public AssignmentResponseBuilder equipmentName(String equipmentName) { this.equipmentName = equipmentName; return this; }
        public AssignmentResponseBuilder equipmentCategory(String equipmentCategory) { this.equipmentCategory = equipmentCategory; return this; }
        public AssignmentResponseBuilder equipmentUnit(String equipmentUnit) { this.equipmentUnit = equipmentUnit; return this; }
        public AssignmentResponseBuilder personnelName(String personnelName) { this.personnelName = personnelName; return this; }
        public AssignmentResponseBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public AssignmentResponseBuilder returnedQuantity(Integer returnedQuantity) { this.returnedQuantity = returnedQuantity; return this; }
        public AssignmentResponseBuilder outstandingQuantity(Integer outstandingQuantity) { this.outstandingQuantity = outstandingQuantity; return this; }
        public AssignmentResponseBuilder assignedDate(LocalDate assignedDate) { this.assignedDate = assignedDate; return this; }
        public AssignmentResponseBuilder status(AssignmentStatus status) { this.status = status; return this; }
        public AssignmentResponseBuilder assignedBy(String assignedBy) { this.assignedBy = assignedBy; return this; }
        public AssignmentResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public AssignmentResponse build() {
            return new AssignmentResponse(id, baseId, baseCode, baseName, equipmentTypeId, equipmentName, equipmentCategory, equipmentUnit, personnelName, quantity, returnedQuantity, outstandingQuantity, assignedDate, status, assignedBy, createdAt);
        }
    }
}
