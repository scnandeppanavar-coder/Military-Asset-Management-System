package com.mams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class EquipmentTypeDTO {

    private Long id;

    @NotBlank(message = "Equipment name is required")
    @Size(min = 2, max = 100, message = "Equipment name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Category is required")
    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
    private String category;

    private String description;

    @NotBlank(message = "Unit of measurement is required")
    @Size(min = 1, max = 20, message = "Unit must be between 1 and 20 characters")
    private String unit;

    private LocalDateTime createdAt;

    public EquipmentTypeDTO() {}

    public EquipmentTypeDTO(Long id, String name, String category, String description, String unit, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.unit = unit;
        this.createdAt = createdAt;
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

    public static EquipmentTypeDTOBuilder builder() { return new EquipmentTypeDTOBuilder(); }

    public static class EquipmentTypeDTOBuilder {
        private Long id;
        private String name;
        private String category;
        private String description;
        private String unit;
        private LocalDateTime createdAt;

        public EquipmentTypeDTOBuilder id(Long id) { this.id = id; return this; }
        public EquipmentTypeDTOBuilder name(String name) { this.name = name; return this; }
        public EquipmentTypeDTOBuilder category(String category) { this.category = category; return this; }
        public EquipmentTypeDTOBuilder description(String description) { this.description = description; return this; }
        public EquipmentTypeDTOBuilder unit(String unit) { this.unit = unit; return this; }
        public EquipmentTypeDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public EquipmentTypeDTO build() {
            return new EquipmentTypeDTO(id, name, category, description, unit, createdAt);
        }
    }
}
