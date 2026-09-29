package com.mams.service;

import com.mams.dto.AssignmentRequest;
import com.mams.dto.AssignmentResponse;
import com.mams.dto.AssignmentReturnRequest;
import com.mams.entity.*;
import com.mams.exception.InsufficientInventoryException;
import com.mams.exception.InvalidOperationException;
import com.mams.exception.ResourceNotFoundException;
import com.mams.repository.AssignmentRepository;
import com.mams.repository.BaseRepository;
import com.mams.repository.EquipmentTypeRepository;
import com.mams.repository.InventoryMovementRepository;
import com.mams.security.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final InventoryMovementRepository movementRepository;
    private final AuditLogService auditLogService;

    public AssignmentService(AssignmentRepository assignmentRepository, BaseRepository baseRepository, EquipmentTypeRepository equipmentTypeRepository, InventoryMovementRepository movementRepository, AuditLogService auditLogService) {
        this.assignmentRepository = assignmentRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.movementRepository = movementRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignments(
            Long baseId,
            Long equipmentTypeId,
            AssignmentStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            String search,
            CustomUserDetails currentUser
    ) {
        if (currentUser.getRole() == Role.LOGISTICS_OFFICER) {
            throw new AccessDeniedException("Logistics officers do not have permission to view assignments.");
        }

        Long effectiveBaseId = baseId;
        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (baseId != null && !baseId.equals(currentUser.getBaseId())) {
                throw new AccessDeniedException("Base commanders may only view assignments for their assigned base.");
            }
            effectiveBaseId = currentUser.getBaseId();
        }

        List<Assignment> assignments = assignmentRepository.findWithFilters(
                effectiveBaseId, equipmentTypeId, status, fromDate, toDate, search
        );

        return assignments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AssignmentResponse getAssignmentById(Long id, CustomUserDetails currentUser) {
        if (currentUser.getRole() == Role.LOGISTICS_OFFICER) {
            throw new AccessDeniedException("Logistics officers do not have permission to view assignments.");
        }

        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + id));

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (!assignment.getBase().getId().equals(currentUser.getBaseId())) {
                throw new AccessDeniedException("Base commanders may only view assignments for their assigned base.");
            }
        }

        return mapToResponse(assignment);
    }

    @Transactional
    public AssignmentResponse createAssignment(AssignmentRequest request, CustomUserDetails currentUser) {
        if (currentUser.getRole() == Role.LOGISTICS_OFFICER) {
            throw new AccessDeniedException("Logistics officers do not have permission to manage assignments.");
        }

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (!request.getBaseId().equals(currentUser.getBaseId())) {
                throw new AccessDeniedException("Base commanders may only assign equipment for their assigned base.");
            }
        }

        Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with ID: " + request.getBaseId()));

        EquipmentType equipmentType = equipmentTypeRepository.findById(request.getEquipmentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment type not found with ID: " + request.getEquipmentTypeId()));

        Long currentStock = movementRepository.calculateAvailableStock(base.getId(), equipmentType.getId());
        if (currentStock < request.getQuantity()) {
            throw new InsufficientInventoryException(String.format(
                    "Insufficient inventory at %s to assign %d %s of %s. Current in-stock: %d",
                    base.getBaseName(), request.getQuantity(), equipmentType.getUnit(), equipmentType.getName(), currentStock
            ));
        }

        Assignment assignment = Assignment.builder()
                .base(base)
                .equipmentType(equipmentType)
                .personnelName(request.getPersonnelName().trim())
                .quantity(request.getQuantity())
                .assignedDate(request.getAssignedDate())
                .returnedQuantity(0)
                .status(AssignmentStatus.ACTIVE)
                .assignedBy(currentUser.getUsername())
                .build();

        Assignment saved = assignmentRepository.save(assignment);

        auditLogService.log(
                currentUser.getUsername(),
                "ASSIGN",
                "ASSIGNMENT",
                saved.getId(),
                String.format("Assigned %d x %s to personnel '%s' at base %s",
                        saved.getQuantity(), equipmentType.getName(), saved.getPersonnelName(), base.getBaseName())
        );

        return mapToResponse(saved);
    }

    @Transactional
    public AssignmentResponse returnEquipment(Long id, AssignmentReturnRequest request, CustomUserDetails currentUser) {
        if (currentUser.getRole() == Role.LOGISTICS_OFFICER) {
            throw new AccessDeniedException("Logistics officers do not have permission to manage assignments.");
        }

        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + id));

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (!assignment.getBase().getId().equals(currentUser.getBaseId())) {
                throw new AccessDeniedException("Base commanders may only manage assignments for their assigned base.");
            }
        }

        int currentReturned = assignment.getReturnedQuantity() != null ? assignment.getReturnedQuantity() : 0;
        int newReturnedTotal = currentReturned + request.getReturnQuantity();

        if (newReturnedTotal > assignment.getQuantity()) {
            throw new InvalidOperationException(String.format(
                    "Total returned quantity (%d) cannot exceed originally assigned quantity (%d). Already returned: %d",
                    newReturnedTotal, assignment.getQuantity(), currentReturned
            ));
        }

        assignment.setReturnedQuantity(newReturnedTotal);
        if (newReturnedTotal == assignment.getQuantity()) {
            assignment.setStatus(AssignmentStatus.RETURNED);
        } else {
            assignment.setStatus(AssignmentStatus.PARTIALLY_RETURNED);
        }

        Assignment updated = assignmentRepository.save(assignment);

        auditLogService.log(
                currentUser.getUsername(),
                "UPDATE",
                "ASSIGNMENT",
                updated.getId(),
                String.format("Recorded return of %d x %s from %s (Total returned: %d/%d, Status: %s)",
                        request.getReturnQuantity(), updated.getEquipmentType().getName(),
                        updated.getPersonnelName(), newReturnedTotal, updated.getQuantity(), updated.getStatus())
        );

        return mapToResponse(updated);
    }

    public AssignmentResponse mapToResponse(Assignment a) {
        int returned = a.getReturnedQuantity() != null ? a.getReturnedQuantity() : 0;
        int outstanding = Math.max(0, a.getQuantity() - returned);

        return AssignmentResponse.builder()
                .id(a.getId())
                .baseId(a.getBase().getId())
                .baseCode(a.getBase().getBaseCode())
                .baseName(a.getBase().getBaseName())
                .equipmentTypeId(a.getEquipmentType().getId())
                .equipmentName(a.getEquipmentType().getName())
                .equipmentCategory(a.getEquipmentType().getCategory())
                .equipmentUnit(a.getEquipmentType().getUnit())
                .personnelName(a.getPersonnelName())
                .quantity(a.getQuantity())
                .returnedQuantity(returned)
                .outstandingQuantity(outstanding)
                .assignedDate(a.getAssignedDate())
                .status(a.getStatus())
                .assignedBy(a.getAssignedBy())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
