package com.mams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class BaseDTO {

    private Long id;

    @NotBlank(message = "Base code is required")
    @Size(min = 2, max = 50, message = "Base code must be between 2 and 50 characters")
    private String baseCode;

    @NotBlank(message = "Base name is required")
    @Size(min = 2, max = 100, message = "Base name must be between 2 and 100 characters")
    private String baseName;

    private String location;
    private String status;
    private LocalDateTime createdAt;

    public BaseDTO() {}

    public BaseDTO(Long id, String baseCode, String baseName, String location, String status, LocalDateTime createdAt) {
        this.id = id;
        this.baseCode = baseCode;
        this.baseName = baseName;
        this.location = location;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBaseCode() { return baseCode; }
    public void setBaseCode(String baseCode) { this.baseCode = baseCode; }

    public String getBaseName() { return baseName; }
    public void setBaseName(String baseName) { this.baseName = baseName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static BaseDTOBuilder builder() { return new BaseDTOBuilder(); }

    public static class BaseDTOBuilder {
        private Long id;
        private String baseCode;
        private String baseName;
        private String location;
        private String status;
        private LocalDateTime createdAt;

        public BaseDTOBuilder id(Long id) { this.id = id; return this; }
        public BaseDTOBuilder baseCode(String baseCode) { this.baseCode = baseCode; return this; }
        public BaseDTOBuilder baseName(String baseName) { this.baseName = baseName; return this; }
        public BaseDTOBuilder location(String location) { this.location = location; return this; }
        public BaseDTOBuilder status(String status) { this.status = status; return this; }
        public BaseDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public BaseDTO build() {
            return new BaseDTO(id, baseCode, baseName, location, status, createdAt);
        }
    }
}
