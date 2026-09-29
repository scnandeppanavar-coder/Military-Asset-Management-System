package com.mams.service;

import com.mams.dto.AuditLogResponse;
import com.mams.entity.AuditLog;
import com.mams.entity.User;
import com.mams.repository.AuditLogRepository;
import com.mams.repository.UserRepository;
import com.mams.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLogService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(User user, String action, String entityType, Long entityId, String description) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .user(user)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .description(description)
                    .ipAddress(IpUtil.getClientIpAddress())
                    .timestamp(LocalDateTime.now())
                    .build();
            auditLogRepository.save(auditLog);
            log.info("AUDIT LOG [{}] {}: {} (User: {})", action, entityType, description, user != null ? user.getUsername() : "SYSTEM");
        } catch (Exception e) {
            log.error("Failed to write audit log: {}", e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String username, String action, String entityType, Long entityId, String description) {
        User user = null;
        if (username != null && !username.isBlank()) {
            user = userRepository.findByUsername(username).orElse(null);
        }
        log(user, action, entityType, entityId, description);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogs(String action, String entityType, LocalDate fromDate, LocalDate toDate, String search) {
        LocalDateTime fromTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toTime = toDate != null ? toDate.atTime(LocalTime.MAX) : null;

        List<AuditLog> logs = auditLogRepository.findWithFilters(action, entityType, fromTime, toTime, search);
        return logs.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public AuditLogResponse mapToResponse(AuditLog l) {
        return AuditLogResponse.builder()
                .id(l.getId())
                .userId(l.getUser() != null ? l.getUser().getId() : null)
                .username(l.getUser() != null ? l.getUser().getUsername() : "SYSTEM")
                .userFullName(l.getUser() != null ? l.getUser().getFullName() : "System Automated")
                .action(l.getAction())
                .entityType(l.getEntityType())
                .entityId(l.getEntityId())
                .description(l.getDescription())
                .ipAddress(l.getIpAddress())
                .timestamp(l.getTimestamp())
                .build();
    }
}
