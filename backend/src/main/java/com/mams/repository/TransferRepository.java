package com.mams.repository;

import com.mams.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Optional<Transfer> findByReferenceNumber(String referenceNumber);
    boolean existsByReferenceNumber(String referenceNumber);

    @Query("SELECT t FROM Transfer t " +
           "WHERE (:involvedBaseId IS NULL OR t.fromBase.id = :involvedBaseId OR t.toBase.id = :involvedBaseId) " +
           "  AND (:fromBaseId IS NULL OR t.fromBase.id = :fromBaseId) " +
           "  AND (:toBaseId IS NULL OR t.toBase.id = :toBaseId) " +
           "  AND (:equipmentTypeId IS NULL OR t.equipmentType.id = :equipmentTypeId) " +
           "  AND (:fromDate IS NULL OR t.transferDate >= :fromDate) " +
           "  AND (:toDate IS NULL OR t.transferDate <= :toDate) " +
           "  AND (:search IS NULL OR LOWER(t.referenceNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "       OR LOWER(t.remarks) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "       OR LOWER(t.equipmentType.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY t.transferDate DESC, t.id DESC")
    List<Transfer> findWithFilters(
            @Param("involvedBaseId") Long involvedBaseId,
            @Param("fromBaseId") Long fromBaseId,
            @Param("toBaseId") Long toBaseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("search") String search
    );
}
