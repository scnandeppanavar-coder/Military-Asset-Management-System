package com.mams.repository;

import com.mams.entity.InventoryMovement;
import com.mams.entity.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

    @Query("SELECT COALESCE(SUM(" +
           "  CASE " +
           "    WHEN im.movementType IN (com.mams.entity.MovementType.PURCHASE, com.mams.entity.MovementType.TRANSFER_IN) THEN im.quantity " +
           "    WHEN im.movementType IN (com.mams.entity.MovementType.TRANSFER_OUT, com.mams.entity.MovementType.EXPENDITURE) THEN -im.quantity " +
           "    ELSE 0 " +
           "  END), 0) " +
           "FROM InventoryMovement im " +
           "WHERE im.base.id = :baseId AND im.equipmentType.id = :equipmentTypeId")
    Long calculateAvailableStock(@Param("baseId") Long baseId, @Param("equipmentTypeId") Long equipmentTypeId);

    @Query("SELECT COALESCE(SUM(" +
           "  CASE " +
           "    WHEN im.movementType IN (com.mams.entity.MovementType.PURCHASE, com.mams.entity.MovementType.TRANSFER_IN) THEN im.quantity " +
           "    WHEN im.movementType IN (com.mams.entity.MovementType.TRANSFER_OUT, com.mams.entity.MovementType.EXPENDITURE) THEN -im.quantity " +
           "    ELSE 0 " +
           "  END), 0) " +
           "FROM InventoryMovement im " +
           "WHERE (:baseId IS NULL OR im.base.id = :baseId) " +
           "  AND (:equipmentTypeId IS NULL OR im.equipmentType.id = :equipmentTypeId) " +
           "  AND im.movementDate < :fromDate")
    Long calculateOpeningBalance(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("fromDate") LocalDate fromDate
    );

    @Query("SELECT COALESCE(SUM(im.quantity), 0) " +
           "FROM InventoryMovement im " +
           "WHERE (:baseId IS NULL OR im.base.id = :baseId) " +
           "  AND (:equipmentTypeId IS NULL OR im.equipmentType.id = :equipmentTypeId) " +
           "  AND im.movementType = :movementType " +
           "  AND (:fromDate IS NULL OR im.movementDate >= :fromDate) " +
           "  AND (:toDate IS NULL OR im.movementDate <= :toDate)")
    Long sumQuantityByMovementType(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("movementType") MovementType movementType,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    List<InventoryMovement> findByBaseIdAndEquipmentTypeId(Long baseId, Long equipmentTypeId);

    void deleteByReferenceIdAndMovementType(Long referenceId, MovementType movementType);
}
