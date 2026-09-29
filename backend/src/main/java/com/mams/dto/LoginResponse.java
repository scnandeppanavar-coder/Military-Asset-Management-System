package com.mams.dto;

public class LoginResponse {
    private String token;
    private String username;
    private String fullName;
    private String role;
    private Long baseId;
    private String baseName;

    public LoginResponse() {}

    public LoginResponse(String token, String username, String fullName, String role, Long baseId, String baseName) {
        this.token = token;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.baseId = baseId;
        this.baseName = baseName;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getBaseId() { return baseId; }
    public void setBaseId(Long baseId) { this.baseId = baseId; }

    public String getBaseName() { return baseName; }
    public void setBaseName(String baseName) { this.baseName = baseName; }

    public static LoginResponseBuilder builder() { return new LoginResponseBuilder(); }

    public static class LoginResponseBuilder {
        private String token;
        private String username;
        private String fullName;
        private String role;
        private Long baseId;
        private String baseName;

        public LoginResponseBuilder token(String token) { this.token = token; return this; }
        public LoginResponseBuilder username(String username) { this.username = username; return this; }
        public LoginResponseBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public LoginResponseBuilder role(String role) { this.role = role; return this; }
        public LoginResponseBuilder baseId(Long baseId) { this.baseId = baseId; return this; }
        public LoginResponseBuilder baseName(String baseName) { this.baseName = baseName; return this; }

        public LoginResponse build() {
            return new LoginResponse(token, username, fullName, role, baseId, baseName);
        }
    }
}
