package com.mams.dto;

import com.mams.entity.MovementType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MovementItemDTO {
    private Long id;
    private Long baseId;
    private String baseName;
    private Long equipmentTypeId;
    private String equipmentName;
    private String equipmentCategory;
    private MovementType movementType;
    private Integer quantity;
    private Long referenceId;
    private LocalDate movementDate;
    private String createdBy;
    private LocalDateTime createdAt;

    public MovementItemDTO() {}

    public MovementItemDTO(Long id, Long baseId, String baseName, Long equipmentTypeId, String equipmentName, String equipmentCategory, MovementType movementType, Integer quantity, Long referenceId, LocalDate movementDate, String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.baseId = baseId;
        this.baseName = baseName;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentName = equipmentName;
        this.equipmentCategory = equipmentCategory;
        this.movementType = movementType;
        this.quantity = quantity;
        this.referenceId = referenceId;
        this.movementDate = movementDate;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBaseId() { return baseId; }
    public void setBaseId(Long baseId) { this.baseId = baseId; }

    public String getBaseName() { return baseName; }
    public void setBaseName(String baseName) { this.baseName = baseName; }

    public Long getEquipmentTypeId() { return equipmentTypeId; }
    public void setEquipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; }

    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }

    public String getEquipmentCategory() { return equipmentCategory; }
    public void setEquipmentCategory(String equipmentCategory) { this.equipmentCategory = equipmentCategory; }

    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public LocalDate getMovementDate() { return movementDate; }
    public void setMovementDate(LocalDate movementDate) { this.movementDate = movementDate; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static MovementItemDTOBuilder builder() { return new MovementItemDTOBuilder(); }

    public static class MovementItemDTOBuilder {
        private Long id;
        private Long baseId;
        private String baseName;
        private Long equipmentTypeId;
        private String equipmentName;
        private String equipmentCategory;
        private MovementType movementType;
        private Integer quantity;
        private Long referenceId;
        private LocalDate movementDate;
        private String createdBy;
        private LocalDateTime createdAt;

        public MovementItemDTOBuilder id(Long id) { this.id = id; return this; }
        public MovementItemDTOBuilder baseId(Long baseId) { this.baseId = baseId; return this; }
        public MovementItemDTOBuilder baseName(String baseName) { this.baseName = baseName; return this; }
        public MovementItemDTOBuilder equipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; return this; }
        public MovementItemDTOBuilder equipmentName(String equipmentName) { this.equipmentName = equipmentName; return this; }
        public MovementItemDTOBuilder equipmentCategory(String equipmentCategory) { this.equipmentCategory = equipmentCategory; return this; }
        public MovementItemDTOBuilder movementType(MovementType movementType) { this.movementType = movementType; return this; }
        public MovementItemDTOBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public MovementItemDTOBuilder referenceId(Long referenceId) { this.referenceId = referenceId; return this; }
        public MovementItemDTOBuilder movementDate(LocalDate movementDate) { this.movementDate = movementDate; return this; }
        public MovementItemDTOBuilder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public MovementItemDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public MovementItemDTO build() {
            return new MovementItemDTO(id, baseId, baseName, equipmentTypeId, equipmentName, equipmentCategory, movementType, quantity, referenceId, movementDate, createdBy, createdAt);
        }
    }
}
