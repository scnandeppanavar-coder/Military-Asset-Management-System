package com.mams.controller;

import com.mams.dto.AssignmentRequest;
import com.mams.dto.AssignmentResponse;
import com.mams.dto.AssignmentReturnRequest;
import com.mams.entity.AssignmentStatus;
import com.mams.security.CustomUserDetails;
import com.mams.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
@Tag(name = "Assignments", description = "Personnel asset assignment and custody tracking endpoints")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    @Operation(summary = "Get all assignments", description = "Retrieves assignments filtered by base, equipment, status, and date")
    public ResponseEntity<List<AssignmentResponse>> getAssignments(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) AssignmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(assignmentService.getAssignments(baseId, equipmentTypeId, status, from, to, search, currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get assignment by ID", description = "Retrieves assignment details by unique ID")
    public ResponseEntity<AssignmentResponse> getAssignmentById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(assignmentService.getAssignmentById(id, currentUser));
    }

    @PostMapping
    @Operation(summary = "Assign equipment to personnel", description = "Checks out equipment to personnel without deducting physical balance")
    public ResponseEntity<AssignmentResponse> createAssignment(
            @Valid @RequestBody AssignmentRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return new ResponseEntity<>(assignmentService.createAssignment(request, currentUser), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Record equipment return", description = "Updates returned quantity and status of an assignment")
    public ResponseEntity<AssignmentResponse> returnEquipment(
            @PathVariable Long id,
            @Valid @RequestBody AssignmentReturnRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(assignmentService.returnEquipment(id, request, currentUser));
    }
}
