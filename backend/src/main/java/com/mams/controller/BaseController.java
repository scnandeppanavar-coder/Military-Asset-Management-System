package com.mams.controller;

import com.mams.dto.BaseDTO;
import com.mams.service.BaseService;
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
@RequestMapping("/api/bases")
@Tag(name = "Bases", description = "Military base management endpoints")
public class BaseController {

    private final BaseService baseService;

    public BaseController(BaseService baseService) {
        this.baseService = baseService;
    }

    @GetMapping
    @Operation(summary = "Get all bases", description = "Retrieves a list of all military bases")
    public ResponseEntity<List<BaseDTO>> getAllBases() {
        return ResponseEntity.ok(baseService.getAllBases());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get base by ID", description = "Retrieves base details by unique ID")
    public ResponseEntity<BaseDTO> getBaseById(@PathVariable Long id) {
        return ResponseEntity.ok(baseService.getBaseById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create base (ADMIN)", description = "Adds a new military installation to the system")
    public ResponseEntity<BaseDTO> createBase(@Valid @RequestBody BaseDTO dto, Authentication auth) {
        return new ResponseEntity<>(baseService.createBase(dto, auth.getName()), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update base (ADMIN)", description = "Updates details of an existing military base")
    public ResponseEntity<BaseDTO> updateBase(
            @PathVariable Long id,
            @Valid @RequestBody BaseDTO dto,
            Authentication auth
    ) {
        return ResponseEntity.ok(baseService.updateBase(id, dto, auth.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete base (ADMIN)", description = "Removes a military base from the system")
    public ResponseEntity<Void> deleteBase(@PathVariable Long id, Authentication auth) {
        baseService.deleteBase(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
