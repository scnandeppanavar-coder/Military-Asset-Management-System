package com.mams.service;

import com.mams.dto.ExpenditureRequest;
import com.mams.dto.ExpenditureResponse;
import com.mams.entity.*;
import com.mams.exception.DuplicateResourceException;
import com.mams.exception.InsufficientInventoryException;
import com.mams.exception.ResourceNotFoundException;
import com.mams.repository.BaseRepository;
import com.mams.repository.EquipmentTypeRepository;
import com.mams.repository.ExpenditureRepository;
import com.mams.repository.InventoryMovementRepository;
import com.mams.security.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExpenditureService {

    private final ExpenditureRepository expenditureRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final InventoryMovementRepository movementRepository;
    private final AuditLogService auditLogService;

    public ExpenditureService(ExpenditureRepository expenditureRepository, BaseRepository baseRepository, EquipmentTypeRepository equipmentTypeRepository, InventoryMovementRepository movementRepository, AuditLogService auditLogService) {
        this.expenditureRepository = expenditureRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.movementRepository = movementRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<ExpenditureResponse> getExpenditures(
            Long baseId,
            Long equipmentTypeId,
            LocalDate fromDate,
            LocalDate toDate,
            String search,
            CustomUserDetails currentUser
    ) {
        if (currentUser.getRole() == Role.LOGISTICS_OFFICER) {
            throw new AccessDeniedException("Logistics officers do not have permission to view expenditures.");
        }

        Long effectiveBaseId = baseId;
        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (baseId != null && !baseId.equals(currentUser.getBaseId())) {
                throw new AccessDeniedException("Base commanders may only view expenditures for their assigned base.");
            }
            effectiveBaseId = currentUser.getBaseId();
        }

        List<Expenditure> expenditures = expenditureRepository.findWithFilters(
                effectiveBaseId, equipmentTypeId, fromDate, toDate, search
        );

        return expenditures.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExpenditureResponse getExpenditureById(Long id, CustomUserDetails currentUser) {
        if (currentUser.getRole() == Role.LOGISTICS_OFFICER) {
            throw new AccessDeniedException("Logistics officers do not have permission to view expenditures.");
        }

        Expenditure expenditure = expenditureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expenditure not found with ID: " + id));

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (!expenditure.getBase().getId().equals(currentUser.getBaseId())) {
                throw new AccessDeniedException("Base commanders may only view expenditures for their assigned base.");
            }
        }

        return mapToResponse(expenditure);
    }

    @Transactional
    public ExpenditureResponse createExpenditure(ExpenditureRequest request, CustomUserDetails currentUser) {
        if (currentUser.getRole() == Role.LOGISTICS_OFFICER) {
            throw new AccessDeniedException("Logistics officers do not have permission to record expenditures.");
        }

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (!request.getBaseId().equals(currentUser.getBaseId())) {
                throw new AccessDeniedException("Base commanders may only record expenditures for their assigned base.");
            }
        }

        Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with ID: " + request.getBaseId()));

        EquipmentType equipmentType = equipmentTypeRepository.findById(request.getEquipmentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment type not found with ID: " + request.getEquipmentTypeId()));

        Long availableStock = movementRepository.calculateAvailableStock(base.getId(), equipmentType.getId());
        if (availableStock < request.getQuantity()) {
            throw new InsufficientInventoryException(String.format(
                    "Cannot record expenditure of %d %s for '%s' at base %s. Available stock: %d",
                    request.getQuantity(), equipmentType.getUnit(), equipmentType.getName(), base.getBaseName(), availableStock
            ));
        }

        String refNum = request.getReferenceNumber();
        if (refNum == null || refNum.isBlank()) {
            refNum = "EXP-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        } else {
            refNum = refNum.trim();
            if (expenditureRepository.existsByReferenceNumber(refNum)) {
                throw new DuplicateResourceException("Expenditure reference number '" + refNum + "' already exists");
            }
        }

        Expenditure expenditure = Expenditure.builder()
                .base(base)
                .equipmentType(equipmentType)
                .quantity(request.getQuantity())
                .expenditureDate(request.getExpenditureDate())
                .reason(request.getReason().trim())
                .referenceNumber(refNum)
                .recordedBy(currentUser.getUsername())
                .build();

        Expenditure saved = expenditureRepository.save(expenditure);

        InventoryMovement movement = InventoryMovement.builder()
                .base(base)
                .equipmentType(equipmentType)
                .movementType(MovementType.EXPENDITURE)
                .quantity(saved.getQuantity())
                .referenceId(saved.getId())
                .movementDate(saved.getExpenditureDate())
                .createdBy(currentUser.getUsername())
                .build();
        movementRepository.save(movement);

        auditLogService.log(
                currentUser.getUsername(),
                "EXPEND",
                "EXPENDITURE",
                saved.getId(),
                String.format("Expended %d x %s at base %s for reason: %s [Ref: %s]",
                        saved.getQuantity(), equipmentType.getName(), base.getBaseName(), saved.getReason(), saved.getReferenceNumber())
        );

        return mapToResponse(saved);
    }

    public ExpenditureResponse mapToResponse(Expenditure e) {
        return ExpenditureResponse.builder()
                .id(e.getId())
                .baseId(e.getBase().getId())
                .baseCode(e.getBase().getBaseCode())
                .baseName(e.getBase().getBaseName())
                .equipmentTypeId(e.getEquipmentType().getId())
                .equipmentName(e.getEquipmentType().getName())
                .equipmentCategory(e.getEquipmentType().getCategory())
                .equipmentUnit(e.getEquipmentType().getUnit())
                .quantity(e.getQuantity())
                .expenditureDate(e.getExpenditureDate())
                .reason(e.getReason())
                .referenceNumber(e.getReferenceNumber())
                .recordedBy(e.getRecordedBy())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
