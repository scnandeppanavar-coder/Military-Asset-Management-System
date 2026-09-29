package com.mams.dto;

import com.mams.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Role is required")
    private Role role;

    private Long baseId;
    private String password;

    public UserUpdateRequest() {}

    public UserUpdateRequest(String fullName, String email, Role role, Long baseId, String password) {
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.baseId = baseId;
        this.password = password;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Long getBaseId() { return baseId; }
    public void setBaseId(Long baseId) { this.baseId = baseId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public static UserUpdateRequestBuilder builder() { return new UserUpdateRequestBuilder(); }

    public static class UserUpdateRequestBuilder {
        private String fullName;
        private String email;
        private Role role;
        private Long baseId;
        private String password;

        public UserUpdateRequestBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public UserUpdateRequestBuilder email(String email) { this.email = email; return this; }
        public UserUpdateRequestBuilder role(Role role) { this.role = role; return this; }
        public UserUpdateRequestBuilder baseId(Long baseId) { this.baseId = baseId; return this; }
        public UserUpdateRequestBuilder password(String password) { this.password = password; return this; }

        public UserUpdateRequest build() {
            return new UserUpdateRequest(fullName, email, role, baseId, password);
        }
    }
}
