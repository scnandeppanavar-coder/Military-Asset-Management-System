package com.mams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipment_types")
public class EquipmentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public EquipmentType() {}

    public EquipmentType(Long id, String name, String category, String description, String unit, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.unit = unit;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static EquipmentTypeBuilder builder() { return new EquipmentTypeBuilder(); }

    public static class EquipmentTypeBuilder {
        private Long id;
        private String name;
        private String category;
        private String description;
        private String unit;
        private LocalDateTime createdAt;

        public EquipmentTypeBuilder id(Long id) { this.id = id; return this; }
        public EquipmentTypeBuilder name(String name) { this.name = name; return this; }
        public EquipmentTypeBuilder category(String category) { this.category = category; return this; }
        public EquipmentTypeBuilder description(String description) { this.description = description; return this; }
        public EquipmentTypeBuilder unit(String unit) { this.unit = unit; return this; }
        public EquipmentTypeBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public EquipmentType build() {
            return new EquipmentType(id, name, category, description, unit, createdAt);
        }
    }
}
