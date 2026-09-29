package com.mams.service;

import com.mams.dto.PurchaseRequest;
import com.mams.dto.PurchaseResponse;
import com.mams.entity.*;
import com.mams.exception.DuplicateResourceException;
import com.mams.exception.InsufficientInventoryException;
import com.mams.exception.ResourceNotFoundException;
import com.mams.repository.BaseRepository;
import com.mams.repository.EquipmentTypeRepository;
import com.mams.repository.InventoryMovementRepository;
import com.mams.repository.PurchaseRepository;
import com.mams.security.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final InventoryMovementRepository movementRepository;
    private final AuditLogService auditLogService;

    public PurchaseService(PurchaseRepository purchaseRepository, BaseRepository baseRepository, EquipmentTypeRepository equipmentTypeRepository, InventoryMovementRepository movementRepository, AuditLogService auditLogService) {
        this.purchaseRepository = purchaseRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.movementRepository = movementRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<PurchaseResponse> getPurchases(
            Long baseId,
            Long equipmentTypeId,
            LocalDate fromDate,
            LocalDate toDate,
            String search,
            CustomUserDetails currentUser
    ) {
        Long effectiveBaseId = baseId;
        if (currentUser != null && currentUser.getRole() == Role.BASE_COMMANDER) {
            if (baseId != null && !baseId.equals(currentUser.getBaseId())) {
                throw new AccessDeniedException("Base commanders may only view purchases for their assigned base.");
            }
            effectiveBaseId = currentUser.getBaseId();
        }

        List<Purchase> purchases = purchaseRepository.findWithFilters(
                effectiveBaseId, equipmentTypeId, fromDate, toDate, search
        );

        return purchases.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PurchaseResponse getPurchaseById(Long id, CustomUserDetails currentUser) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with ID: " + id));

        if (currentUser != null && currentUser.getRole() == Role.BASE_COMMANDER) {
            if (!purchase.getBase().getId().equals(currentUser.getBaseId())) {
                throw new AccessDeniedException("Base commanders may only view purchases for their assigned base.");
            }
        }

        return mapToResponse(purchase);
    }

    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request, CustomUserDetails currentUser) {
        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (!request.getBaseId().equals(currentUser.getBaseId())) {
                throw new AccessDeniedException("Base commanders may only create purchases for their assigned base.");
            }
        }

        Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with ID: " + request.getBaseId()));

        EquipmentType equipmentType = equipmentTypeRepository.findById(request.getEquipmentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment type not found with ID: " + request.getEquipmentTypeId()));

        String refNum = request.getReferenceNumber();
        if (refNum == null || refNum.isBlank()) {
            refNum = "PUR-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        } else {
            refNum = refNum.trim();
            if (purchaseRepository.existsByReferenceNumber(refNum)) {
                throw new DuplicateResourceException("Purchase reference number '" + refNum + "' already exists");
            }
        }

        Purchase purchase = Purchase.builder()
                .base(base)
                .equipmentType(equipmentType)
                .quantity(request.getQuantity())
                .purchaseDate(request.getPurchaseDate())
                .referenceNumber(refNum)
                .remarks(request.getRemarks())
                .createdBy(currentUser.getUsername())
                .build();

        Purchase savedPurchase = purchaseRepository.save(purchase);

        InventoryMovement movement = InventoryMovement.builder()
                .base(base)
                .equipmentType(equipmentType)
                .movementType(MovementType.PURCHASE)
                .quantity(savedPurchase.getQuantity())
                .referenceId(savedPurchase.getId())
                .movementDate(savedPurchase.getPurchaseDate())
                .createdBy(currentUser.getUsername())
                .build();
        movementRepository.save(movement);

        auditLogService.log(
                currentUser.getUsername(),
                "PURCHASE",
                "PURCHASE",
                savedPurchase.getId(),
                String.format("Acquired %d x %s for base %s [Ref: %s]",
                        savedPurchase.getQuantity(), equipmentType.getName(), base.getBaseName(), savedPurchase.getReferenceNumber())
        );

        return mapToResponse(savedPurchase);
    }

    @Transactional
    public PurchaseResponse updatePurchase(Long id, PurchaseRequest request, CustomUserDetails currentUser) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with ID: " + id));

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (!purchase.getBase().getId().equals(currentUser.getBaseId()) || !request.getBaseId().equals(currentUser.getBaseId())) {
                throw new AccessDeniedException("Base commanders may only update purchases for their assigned base.");
            }
        }

        if (request.getReferenceNumber() != null && !request.getReferenceNumber().equalsIgnoreCase(purchase.getReferenceNumber())) {
            if (purchaseRepository.existsByReferenceNumber(request.getReferenceNumber())) {
                throw new DuplicateResourceException("Purchase reference number '" + request.getReferenceNumber() + "' already exists");
            }
            purchase.setReferenceNumber(request.getReferenceNumber().trim());
        }

        Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with ID: " + request.getBaseId()));
        EquipmentType equipmentType = equipmentTypeRepository.findById(request.getEquipmentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment type not found with ID: " + request.getEquipmentTypeId()));

        purchase.setBase(base);
        purchase.setEquipmentType(equipmentType);
        purchase.setQuantity(request.getQuantity());
        purchase.setPurchaseDate(request.getPurchaseDate());
        purchase.setRemarks(request.getRemarks());

        Purchase updatedPurchase = purchaseRepository.save(purchase);

        movementRepository.deleteByReferenceIdAndMovementType(updatedPurchase.getId(), MovementType.PURCHASE);
        InventoryMovement movement = InventoryMovement.builder()
                .base(base)
                .equipmentType(equipmentType)
                .movementType(MovementType.PURCHASE)
                .quantity(updatedPurchase.getQuantity())
                .referenceId(updatedPurchase.getId())
                .movementDate(updatedPurchase.getPurchaseDate())
                .createdBy(currentUser.getUsername())
                .build();
        movementRepository.save(movement);

        auditLogService.log(
                currentUser.getUsername(),
                "UPDATE",
                "PURCHASE",
                updatedPurchase.getId(),
                String.format("Updated purchase %s: %d x %s for base %s",
                        updatedPurchase.getReferenceNumber(), updatedPurchase.getQuantity(), equipmentType.getName(), base.getBaseName())
        );

        return mapToResponse(updatedPurchase);
    }

    @Transactional
    public void deletePurchase(Long id, CustomUserDetails currentUser) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with ID: " + id));

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (!purchase.getBase().getId().equals(currentUser.getBaseId())) {
                throw new AccessDeniedException("Base commanders may only delete purchases for their assigned base.");
            }
        }

        Long currentStock = movementRepository.calculateAvailableStock(
                purchase.getBase().getId(), purchase.getEquipmentType().getId()
        );
        if (currentStock < purchase.getQuantity()) {
            throw new InsufficientInventoryException(
                    "Cannot delete purchase: Current available inventory (" + currentStock +
                    ") is less than purchase quantity (" + purchase.getQuantity() + "). Assets have already been transferred or expended."
            );
        }

        movementRepository.deleteByReferenceIdAndMovementType(purchase.getId(), MovementType.PURCHASE);
        purchaseRepository.delete(purchase);

        auditLogService.log(
                currentUser.getUsername(),
                "DELETE",
                "PURCHASE",
                id,
                String.format("Deleted purchase %s (%d x %s)",
                        purchase.getReferenceNumber(), purchase.getQuantity(), purchase.getEquipmentType().getName())
        );
    }

    public PurchaseResponse mapToResponse(Purchase p) {
        return PurchaseResponse.builder()
                .id(p.getId())
                .baseId(p.getBase().getId())
                .baseCode(p.getBase().getBaseCode())
                .baseName(p.getBase().getBaseName())
                .equipmentTypeId(p.getEquipmentType().getId())
                .equipmentName(p.getEquipmentType().getName())
                .equipmentCategory(p.getEquipmentType().getCategory())
                .equipmentUnit(p.getEquipmentType().getUnit())
                .quantity(p.getQuantity())
                .purchaseDate(p.getPurchaseDate())
                .referenceNumber(p.getReferenceNumber())
                .remarks(p.getRemarks())
                .createdBy(p.getCreatedBy())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
