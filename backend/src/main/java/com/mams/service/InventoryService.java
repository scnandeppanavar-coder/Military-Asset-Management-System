package com.mams.service;

import com.mams.dto.DashboardSummaryResponse;
import com.mams.dto.MovementItemDTO;
import com.mams.dto.NetMovementBreakdownResponse;
import com.mams.entity.Base;
import com.mams.entity.EquipmentType;
import com.mams.entity.InventoryMovement;
import com.mams.entity.MovementType;
import com.mams.entity.Role;
import com.mams.repository.AssignmentRepository;
import com.mams.repository.BaseRepository;
import com.mams.repository.EquipmentTypeRepository;
import com.mams.repository.InventoryMovementRepository;
import com.mams.security.CustomUserDetails;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryMovementRepository movementRepository;
    private final AssignmentRepository assignmentRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;

    public InventoryService(InventoryMovementRepository movementRepository, AssignmentRepository assignmentRepository, BaseRepository baseRepository, EquipmentTypeRepository equipmentTypeRepository) {
        this.movementRepository = movementRepository;
        this.assignmentRepository = assignmentRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary(
            Long baseId,
            Long equipmentTypeId,
            LocalDate from,
            LocalDate to,
            CustomUserDetails currentUser
    ) {
        Long effectiveBaseId = determineEffectiveBaseId(baseId, currentUser);

        long openingBalance = 0L;
        if (from != null) {
            openingBalance = movementRepository.calculateOpeningBalance(effectiveBaseId, equipmentTypeId, from);
        }

        long purchases = movementRepository.sumQuantityByMovementType(
                effectiveBaseId, equipmentTypeId, MovementType.PURCHASE, from, to
        );
        long transferIn = movementRepository.sumQuantityByMovementType(
                effectiveBaseId, equipmentTypeId, MovementType.TRANSFER_IN, from, to
        );
        long transferOut = movementRepository.sumQuantityByMovementType(
                effectiveBaseId, equipmentTypeId, MovementType.TRANSFER_OUT, from, to
        );

        long netMovement = purchases + transferIn - transferOut;

        long expended = movementRepository.sumQuantityByMovementType(
                effectiveBaseId, equipmentTypeId, MovementType.EXPENDITURE, from, to
        );

        long closingBalance = openingBalance + netMovement - expended;

        long assigned = assignmentRepository.calculateCurrentlyAssigned(
                effectiveBaseId, equipmentTypeId, from, to
        );

        String baseName = null;
        if (effectiveBaseId != null) {
            baseName = baseRepository.findById(effectiveBaseId)
                    .map(Base::getBaseName).orElse(null);
        }

        String equipmentName = null;
        if (equipmentTypeId != null) {
            equipmentName = equipmentTypeRepository.findById(equipmentTypeId)
                    .map(EquipmentType::getName).orElse(null);
        }

        List<MovementItemDTO> recentMovements = movementRepository.findAll(Sort.by(Sort.Direction.DESC, "movementDate", "id"))
                .stream()
                .filter(m -> (effectiveBaseId == null || m.getBase().getId().equals(effectiveBaseId)) &&
                             (equipmentTypeId == null || m.getEquipmentType().getId().equals(equipmentTypeId)) &&
                             (from == null || !m.getMovementDate().isBefore(from)) &&
                             (to == null || !m.getMovementDate().isAfter(to)))
                .limit(10)
                .map(this::mapToMovementDTO)
                .collect(Collectors.toList());

        return DashboardSummaryResponse.builder()
                .openingBalance(openingBalance)
                .purchases(purchases)
                .transferIn(transferIn)
                .transferOut(transferOut)
                .netMovement(netMovement)
                .expended(expended)
                .closingBalance(closingBalance)
                .assigned(assigned)
                .baseId(effectiveBaseId)
                .baseName(baseName)
                .equipmentTypeId(equipmentTypeId)
                .equipmentName(equipmentName)
                .from(from)
                .to(to)
                .recentMovements(recentMovements)
                .build();
    }

    @Transactional(readOnly = true)
    public NetMovementBreakdownResponse getNetMovementBreakdown(
            Long baseId,
            Long equipmentTypeId,
            LocalDate from,
            LocalDate to,
            CustomUserDetails currentUser
    ) {
        Long effectiveBaseId = determineEffectiveBaseId(baseId, currentUser);

        long purchases = movementRepository.sumQuantityByMovementType(
                effectiveBaseId, equipmentTypeId, MovementType.PURCHASE, from, to
        );
        long transferIn = movementRepository.sumQuantityByMovementType(
                effectiveBaseId, equipmentTypeId, MovementType.TRANSFER_IN, from, to
        );
        long transferOut = movementRepository.sumQuantityByMovementType(
                effectiveBaseId, equipmentTypeId, MovementType.TRANSFER_OUT, from, to
        );

        long netMovement = purchases + transferIn - transferOut;

        String baseName = null;
        if (effectiveBaseId != null) {
            baseName = baseRepository.findById(effectiveBaseId)
                    .map(Base::getBaseName).orElse(null);
        }

        String equipmentName = null;
        if (equipmentTypeId != null) {
            equipmentName = equipmentTypeRepository.findById(equipmentTypeId)
                    .map(EquipmentType::getName).orElse(null);
        }

        return NetMovementBreakdownResponse.builder()
                .purchases(purchases)
                .transferIn(transferIn)
                .transferOut(transferOut)
                .netMovement(netMovement)
                .baseId(effectiveBaseId)
                .baseName(baseName)
                .equipmentTypeId(equipmentTypeId)
                .equipmentName(equipmentName)
                .from(from)
                .to(to)
                .build();
    }

    private Long determineEffectiveBaseId(Long requestedBaseId, CustomUserDetails currentUser) {
        if (currentUser != null && currentUser.getRole() == Role.BASE_COMMANDER) {
            if (requestedBaseId != null && !requestedBaseId.equals(currentUser.getBaseId())) {
                throw new AccessDeniedException("Base commanders may only access data for their assigned base.");
            }
            return currentUser.getBaseId();
        }
        return requestedBaseId;
    }

    public MovementItemDTO mapToMovementDTO(InventoryMovement m) {
        return MovementItemDTO.builder()
                .id(m.getId())
                .baseId(m.getBase().getId())
                .baseName(m.getBase().getBaseName())
                .equipmentTypeId(m.getEquipmentType().getId())
                .equipmentName(m.getEquipmentType().getName())
                .equipmentCategory(m.getEquipmentType().getCategory())
                .movementType(m.getMovementType())
                .quantity(m.getQuantity())
                .referenceId(m.getReferenceId())
                .movementDate(m.getMovementDate())
                .createdBy(m.getCreatedBy())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
