package com.mams.repository;

import com.mams.entity.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    Optional<Expenditure> findByReferenceNumber(String referenceNumber);
    boolean existsByReferenceNumber(String referenceNumber);

    @Query("SELECT e FROM Expenditure e " +
           "WHERE (:baseId IS NULL OR e.base.id = :baseId) " +
           "  AND (:equipmentTypeId IS NULL OR e.equipmentType.id = :equipmentTypeId) " +
           "  AND (:fromDate IS NULL OR e.expenditureDate >= :fromDate) " +
           "  AND (:toDate IS NULL OR e.expenditureDate <= :toDate) " +
           "  AND (:search IS NULL OR LOWER(e.referenceNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "       OR LOWER(e.reason) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "       OR LOWER(e.equipmentType.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY e.expenditureDate DESC, e.id DESC")
    List<Expenditure> findWithFilters(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("search") String search
    );
}
