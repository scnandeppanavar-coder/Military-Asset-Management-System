package com.mams.service;

import com.mams.dto.TransferRequest;
import com.mams.dto.TransferResponse;
import com.mams.entity.*;
import com.mams.exception.DuplicateResourceException;
import com.mams.exception.InsufficientInventoryException;
import com.mams.exception.InvalidOperationException;
import com.mams.exception.ResourceNotFoundException;
import com.mams.repository.BaseRepository;
import com.mams.repository.EquipmentTypeRepository;
import com.mams.repository.InventoryMovementRepository;
import com.mams.repository.TransferRepository;
import com.mams.security.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final InventoryMovementRepository movementRepository;
    private final AuditLogService auditLogService;

    public TransferService(TransferRepository transferRepository, BaseRepository baseRepository, EquipmentTypeRepository equipmentTypeRepository, InventoryMovementRepository movementRepository, AuditLogService auditLogService) {
        this.transferRepository = transferRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.movementRepository = movementRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<TransferResponse> getTransfers(
            Long fromBaseId,
            Long toBaseId,
            Long equipmentTypeId,
            LocalDate fromDate,
            LocalDate toDate,
            String search,
            CustomUserDetails currentUser
    ) {
        Long involvedBaseId = null;
        if (currentUser != null && currentUser.getRole() == Role.BASE_COMMANDER) {
            involvedBaseId = currentUser.getBaseId();
        }

        List<Transfer> transfers = transferRepository.findWithFilters(
                involvedBaseId, fromBaseId, toBaseId, equipmentTypeId, fromDate, toDate, search
        );

        return transfers.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransferResponse getTransferById(Long id, CustomUserDetails currentUser) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found with ID: " + id));

        if (currentUser != null && currentUser.getRole() == Role.BASE_COMMANDER) {
            Long baseId = currentUser.getBaseId();
            if (!transfer.getFromBase().getId().equals(baseId) && !transfer.getToBase().getId().equals(baseId)) {
                throw new AccessDeniedException("Base commanders may only view transfers involving their assigned base.");
            }
        }

        return mapToResponse(transfer);
    }

    @Transactional
    public TransferResponse createTransfer(TransferRequest request, CustomUserDetails currentUser) {
        if (request.getFromBaseId().equals(request.getToBaseId())) {
            throw new InvalidOperationException("Source base and destination base cannot be the same.");
        }

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            Long userBaseId = currentUser.getBaseId();
            if (!request.getFromBaseId().equals(userBaseId) && !request.getToBaseId().equals(userBaseId)) {
                throw new AccessDeniedException("Base commanders may only initiate transfers involving their assigned base.");
            }
        }

        Base fromBase = baseRepository.findById(request.getFromBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Source base not found with ID: " + request.getFromBaseId()));

        Base toBase = baseRepository.findById(request.getToBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination base not found with ID: " + request.getToBaseId()));

        EquipmentType equipmentType = equipmentTypeRepository.findById(request.getEquipmentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment type not found with ID: " + request.getEquipmentTypeId()));

        Long availableStock = movementRepository.calculateAvailableStock(fromBase.getId(), equipmentType.getId());
        if (availableStock < request.getQuantity()) {
            throw new InsufficientInventoryException(String.format(
                    "Insufficient inventory at source base '%s'. Available: %d %s, Requested: %d %s",
                    fromBase.getBaseName(), availableStock, equipmentType.getUnit(), request.getQuantity(), equipmentType.getUnit()
            ));
        }

        String refNum = request.getReferenceNumber();
        if (refNum == null || refNum.isBlank()) {
            refNum = "TRF-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        } else {
            refNum = refNum.trim();
            if (transferRepository.existsByReferenceNumber(refNum)) {
                throw new DuplicateResourceException("Transfer reference number '" + refNum + "' already exists");
            }
        }

        Transfer transfer = Transfer.builder()
                .fromBase(fromBase)
                .toBase(toBase)
                .equipmentType(equipmentType)
                .quantity(request.getQuantity())
                .transferDate(request.getTransferDate())
                .referenceNumber(refNum)
                .status("COMPLETED")
                .remarks(request.getRemarks())
                .createdBy(currentUser.getUsername())
                .build();

        Transfer savedTransfer = transferRepository.save(transfer);

        InventoryMovement movementOut = InventoryMovement.builder()
                .base(fromBase)
                .equipmentType(equipmentType)
                .movementType(MovementType.TRANSFER_OUT)
                .quantity(savedTransfer.getQuantity())
                .referenceId(savedTransfer.getId())
                .movementDate(savedTransfer.getTransferDate())
                .createdBy(currentUser.getUsername())
                .build();
        movementRepository.save(movementOut);

        InventoryMovement movementIn = InventoryMovement.builder()
                .base(toBase)
                .equipmentType(equipmentType)
                .movementType(MovementType.TRANSFER_IN)
                .quantity(savedTransfer.getQuantity())
                .referenceId(savedTransfer.getId())
                .movementDate(savedTransfer.getTransferDate())
                .createdBy(currentUser.getUsername())
                .build();
        movementRepository.save(movementIn);

        auditLogService.log(
                currentUser.getUsername(),
                "TRANSFER",
                "TRANSFER",
                savedTransfer.getId(),
                String.format("Transferred %d x %s from %s to %s [Ref: %s]",
                        savedTransfer.getQuantity(), equipmentType.getName(), fromBase.getBaseName(), toBase.getBaseName(), savedTransfer.getReferenceNumber())
        );

        return mapToResponse(savedTransfer);
    }

    public TransferResponse mapToResponse(Transfer t) {
        return TransferResponse.builder()
                .id(t.getId())
                .fromBaseId(t.getFromBase().getId())
                .fromBaseCode(t.getFromBase().getBaseCode())
                .fromBaseName(t.getFromBase().getBaseName())
                .toBaseId(t.getToBase().getId())
                .toBaseCode(t.getToBase().getBaseCode())
                .toBaseName(t.getToBase().getBaseName())
                .equipmentTypeId(t.getEquipmentType().getId())
                .equipmentName(t.getEquipmentType().getName())
                .equipmentCategory(t.getEquipmentType().getCategory())
                .equipmentUnit(t.getEquipmentType().getUnit())
                .quantity(t.getQuantity())
                .transferDate(t.getTransferDate())
                .referenceNumber(t.getReferenceNumber())
                .status(t.getStatus())
                .remarks(t.getRemarks())
                .createdBy(t.getCreatedBy())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
