package com.mams.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_type_id", nullable = false)
    private EquipmentType equipmentType;

    @Column(name = "personnel_name", nullable = false, length = 100)
    private String personnelName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "assigned_date", nullable = false)
    private LocalDate assignedDate;

    @Column(name = "returned_quantity", nullable = false)
    private Integer returnedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AssignmentStatus status;

    @Column(name = "assigned_by", nullable = false, length = 50)
    private String assignedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Assignment() {}

    public Assignment(Long id, Base base, EquipmentType equipmentType, String personnelName, Integer quantity, LocalDate assignedDate, Integer returnedQuantity, AssignmentStatus status, String assignedBy, LocalDateTime createdAt) {
        this.id = id;
        this.base = base;
        this.equipmentType = equipmentType;
        this.personnelName = personnelName;
        this.quantity = quantity;
        this.assignedDate = assignedDate;
        this.returnedQuantity = returnedQuantity != null ? returnedQuantity : 0;
        this.status = status != null ? status : AssignmentStatus.ACTIVE;
        this.assignedBy = assignedBy;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (returnedQuantity == null) {
            returnedQuantity = 0;
        }
        if (status == null) {
            status = AssignmentStatus.ACTIVE;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Base getBase() { return base; }
    public void setBase(Base base) { this.base = base; }

    public EquipmentType getEquipmentType() { return equipmentType; }
    public void setEquipmentType(EquipmentType equipmentType) { this.equipmentType = equipmentType; }

    public String getPersonnelName() { return personnelName; }
    public void setPersonnelName(String personnelName) { this.personnelName = personnelName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDate assignedDate) { this.assignedDate = assignedDate; }

    public Integer getReturnedQuantity() { return returnedQuantity; }
    public void setReturnedQuantity(Integer returnedQuantity) { this.returnedQuantity = returnedQuantity; }

    public AssignmentStatus getStatus() { return status; }
    public void setStatus(AssignmentStatus status) { this.status = status; }

    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static AssignmentBuilder builder() { return new AssignmentBuilder(); }

    public static class AssignmentBuilder {
        private Long id;
        private Base base;
        private EquipmentType equipmentType;
        private String personnelName;
        private Integer quantity;
        private LocalDate assignedDate;
        private Integer returnedQuantity;
        private AssignmentStatus status;
        private String assignedBy;
        private LocalDateTime createdAt;

        public AssignmentBuilder id(Long id) { this.id = id; return this; }
        public AssignmentBuilder base(Base base) { this.base = base; return this; }
        public AssignmentBuilder equipmentType(EquipmentType equipmentType) { this.equipmentType = equipmentType; return this; }
        public AssignmentBuilder personnelName(String personnelName) { this.personnelName = personnelName; return this; }
        public AssignmentBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public AssignmentBuilder assignedDate(LocalDate assignedDate) { this.assignedDate = assignedDate; return this; }
        public AssignmentBuilder returnedQuantity(Integer returnedQuantity) { this.returnedQuantity = returnedQuantity; return this; }
        public AssignmentBuilder status(AssignmentStatus status) { this.status = status; return this; }
        public AssignmentBuilder assignedBy(String assignedBy) { this.assignedBy = assignedBy; return this; }
        public AssignmentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Assignment build() {
            return new Assignment(id, base, equipmentType, personnelName, quantity, assignedDate, returnedQuantity, status, assignedBy, createdAt);
        }
    }
}
