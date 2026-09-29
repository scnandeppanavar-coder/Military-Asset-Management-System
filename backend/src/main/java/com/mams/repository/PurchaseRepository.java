package com.mams.repository;

import com.mams.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Optional<Purchase> findByReferenceNumber(String referenceNumber);
    boolean existsByReferenceNumber(String referenceNumber);

    @Query("SELECT p FROM Purchase p " +
           "WHERE (:baseId IS NULL OR p.base.id = :baseId) " +
           "  AND (:equipmentTypeId IS NULL OR p.equipmentType.id = :equipmentTypeId) " +
           "  AND (:fromDate IS NULL OR p.purchaseDate >= :fromDate) " +
           "  AND (:toDate IS NULL OR p.purchaseDate <= :toDate) " +
           "  AND (:search IS NULL OR LOWER(p.referenceNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "       OR LOWER(p.remarks) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "       OR LOWER(p.equipmentType.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY p.purchaseDate DESC, p.id DESC")
    List<Purchase> findWithFilters(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("search") String search
    );
}
