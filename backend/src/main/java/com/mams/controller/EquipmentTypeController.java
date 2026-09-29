package com.mams.controller;

import com.mams.dto.EquipmentTypeDTO;
import com.mams.service.EquipmentTypeService;
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
@RequestMapping("/api/equipment-types")
@Tag(name = "Equipment Types", description = "Military equipment types catalog management")
public class EquipmentTypeController {

    private final EquipmentTypeService equipmentTypeService;

    public EquipmentTypeController(EquipmentTypeService equipmentTypeService) {
        this.equipmentTypeService = equipmentTypeService;
    }

    @GetMapping
    @Operation(summary = "Get all equipment types", description = "Retrieves all equipment specifications across categories")
    public ResponseEntity<List<EquipmentTypeDTO>> getAllEquipmentTypes() {
        return ResponseEntity.ok(equipmentTypeService.getAllEquipmentTypes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get equipment type by ID", description = "Retrieves equipment details by unique ID")
    public ResponseEntity<EquipmentTypeDTO> getEquipmentTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentTypeService.getEquipmentTypeById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create equipment type (ADMIN)", description = "Adds a new equipment model to the inventory catalog")
    public ResponseEntity<EquipmentTypeDTO> createEquipmentType(
            @Valid @RequestBody EquipmentTypeDTO dto,
            Authentication auth
    ) {
        return new ResponseEntity<>(equipmentTypeService.createEquipmentType(dto, auth.getName()), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update equipment type (ADMIN)", description = "Updates details of an existing equipment model")
    public ResponseEntity<EquipmentTypeDTO> updateEquipmentType(
            @PathVariable Long id,
            @Valid @RequestBody EquipmentTypeDTO dto,
            Authentication auth
    ) {
        return ResponseEntity.ok(equipmentTypeService.updateEquipmentType(id, dto, auth.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete equipment type (ADMIN)", description = "Removes an equipment model from the catalog")
    public ResponseEntity<Void> deleteEquipmentType(@PathVariable Long id, Authentication auth) {
        equipmentTypeService.deleteEquipmentType(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
