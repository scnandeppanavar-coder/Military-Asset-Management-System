package com.mams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bases")
public class Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "base_code", unique = true, nullable = false, length = 50)
    private String baseCode;

    @Column(name = "base_name", nullable = false, length = 100)
    private String baseName;

    @Column(length = 150)
    private String location;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Base() {}

    public Base(Long id, String baseCode, String baseName, String location, String status, LocalDateTime createdAt) {
        this.id = id;
        this.baseCode = baseCode;
        this.baseName = baseName;
        this.location = location;
        this.status = status;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "ACTIVE";
        }
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

    public static BaseBuilder builder() { return new BaseBuilder(); }

    public static class BaseBuilder {
        private Long id;
        private String baseCode;
        private String baseName;
        private String location;
        private String status;
        private LocalDateTime createdAt;

        public BaseBuilder id(Long id) { this.id = id; return this; }
        public BaseBuilder baseCode(String baseCode) { this.baseCode = baseCode; return this; }
        public BaseBuilder baseName(String baseName) { this.baseName = baseName; return this; }
        public BaseBuilder location(String location) { this.location = location; return this; }
        public BaseBuilder status(String status) { this.status = status; return this; }
        public BaseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Base build() {
            return new Base(id, baseCode, baseName, location, status, createdAt);
        }
    }
}
