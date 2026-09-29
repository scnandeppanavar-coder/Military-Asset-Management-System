package com.mams.dto;

import java.time.LocalDateTime;

public class AuditLogResponse {
    private Long id;
    private Long userId;
    private String username;
    private String userFullName;
    private String action;
    private String entityType;
    private Long entityId;
    private String description;
    private String ipAddress;
    private LocalDateTime timestamp;

    public AuditLogResponse() {}

    public AuditLogResponse(Long id, Long userId, String username, String userFullName, String action, String entityType, Long entityId, String description, String ipAddress, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.userFullName = userFullName;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.description = description;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public static AuditLogResponseBuilder builder() { return new AuditLogResponseBuilder(); }

    public static class AuditLogResponseBuilder {
        private Long id;
        private Long userId;
        private String username;
        private String userFullName;
        private String action;
        private String entityType;
        private Long entityId;
        private String description;
        private String ipAddress;
        private LocalDateTime timestamp;

        public AuditLogResponseBuilder id(Long id) { this.id = id; return this; }
        public AuditLogResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public AuditLogResponseBuilder username(String username) { this.username = username; return this; }
        public AuditLogResponseBuilder userFullName(String userFullName) { this.userFullName = userFullName; return this; }
        public AuditLogResponseBuilder action(String action) { this.action = action; return this; }
        public AuditLogResponseBuilder entityType(String entityType) { this.entityType = entityType; return this; }
        public AuditLogResponseBuilder entityId(Long entityId) { this.entityId = entityId; return this; }
        public AuditLogResponseBuilder description(String description) { this.description = description; return this; }
        public AuditLogResponseBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public AuditLogResponseBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public AuditLogResponse build() {
            return new AuditLogResponse(id, userId, username, userFullName, action, entityType, entityId, description, ipAddress, timestamp);
        }
    }
}
