package com.mams.dto;

import com.mams.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserCreateRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Role is required")
    private Role role;

    private Long baseId;

    public UserCreateRequest() {}

    public UserCreateRequest(String username, String password, String fullName, String email, Role role, Long baseId) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.baseId = baseId;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Long getBaseId() { return baseId; }
    public void setBaseId(Long baseId) { this.baseId = baseId; }

    public static UserCreateRequestBuilder builder() { return new UserCreateRequestBuilder(); }

    public static class UserCreateRequestBuilder {
        private String username;
        private String password;
        private String fullName;
        private String email;
        private Role role;
        private Long baseId;

        public UserCreateRequestBuilder username(String username) { this.username = username; return this; }
        public UserCreateRequestBuilder password(String password) { this.password = password; return this; }
        public UserCreateRequestBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public UserCreateRequestBuilder email(String email) { this.email = email; return this; }
        public UserCreateRequestBuilder role(Role role) { this.role = role; return this; }
        public UserCreateRequestBuilder baseId(Long baseId) { this.baseId = baseId; return this; }

        public UserCreateRequest build() {
            return new UserCreateRequest(username, password, fullName, email, role, baseId);
        }
    }
}
