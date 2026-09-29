package com.mams.repository;

import com.mams.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT a FROM AuditLog a " +
           "WHERE (:action IS NULL OR a.action = :action) " +
           "  AND (:entityType IS NULL OR a.entityType = :entityType) " +
           "  AND (:fromTime IS NULL OR a.timestamp >= :fromTime) " +
           "  AND (:toTime IS NULL OR a.timestamp <= :toTime) " +
           "  AND (:search IS NULL OR LOWER(a.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "       OR LOWER(a.user.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "       OR LOWER(a.action) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "       OR LOWER(a.entityType) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY a.timestamp DESC, a.id DESC")
    List<AuditLog> findWithFilters(
            @Param("action") String action,
            @Param("entityType") String entityType,
            @Param("fromTime") LocalDateTime fromTime,
            @Param("toTime") LocalDateTime toTime,
            @Param("search") String search
    );

    List<AuditLog> findTop100ByOrderByTimestampDesc();
}
