package com.mams.repository;

import com.mams.entity.Assignment;
import com.mams.entity.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @Query("SELECT a FROM Assignment a " +
           "WHERE (:baseId IS NULL OR a.base.id = :baseId) " +
           "  AND (:equipmentTypeId IS NULL OR a.equipmentType.id = :equipmentTypeId) " +
           "  AND (:status IS NULL OR a.status = :status) " +
           "  AND (:fromDate IS NULL OR a.assignedDate >= :fromDate) " +
           "  AND (:toDate IS NULL OR a.assignedDate <= :toDate) " +
           "  AND (:search IS NULL OR LOWER(a.personnelName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "       OR LOWER(a.equipmentType.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY a.assignedDate DESC, a.id DESC")
    List<Assignment> findWithFilters(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("status") AssignmentStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("search") String search
    );

    @Query("SELECT COALESCE(SUM(a.quantity - a.returnedQuantity), 0) FROM Assignment a " +
           "WHERE (:baseId IS NULL OR a.base.id = :baseId) " +
           "  AND (:equipmentTypeId IS NULL OR a.equipmentType.id = :equipmentTypeId) " +
           "  AND a.status != com.mams.entity.AssignmentStatus.RETURNED " +
           "  AND (:fromDate IS NULL OR a.assignedDate >= :fromDate) " +
           "  AND (:toDate IS NULL OR a.assignedDate <= :toDate)")
    Long calculateCurrentlyAssigned(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );
}
