package com.mams.controller;

import com.mams.dto.UserCreateRequest;
import com.mams.dto.UserDTO;
import com.mams.dto.UserUpdateRequest;
import com.mams.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Users", description = "User administration and access control (Admin Only)")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all users (ADMIN)", description = "Retrieves all system user accounts")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID (ADMIN)", description = "Retrieves details of a specific user account")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @Operation(summary = "Create user (ADMIN)", description = "Creates a new user with specific role and base assignment")
    public ResponseEntity<UserDTO> createUser(
            @Valid @RequestBody UserCreateRequest request,
            Authentication auth
    ) {
        return new ResponseEntity<>(userService.createUser(request, auth.getName()), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user (ADMIN)", description = "Modifies user details, role, or base assignment")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(userService.updateUser(id, request, auth.getName()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user (ADMIN)", description = "Deactivates and removes a user account")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication auth) {
        userService.deleteUser(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
