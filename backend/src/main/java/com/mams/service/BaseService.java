package com.mams.service;

import com.mams.dto.BaseDTO;
import com.mams.entity.Base;
import com.mams.exception.DuplicateResourceException;
import com.mams.exception.ResourceNotFoundException;
import com.mams.repository.BaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BaseService {

    private final BaseRepository baseRepository;
    private final AuditLogService auditLogService;

    public BaseService(BaseRepository baseRepository, AuditLogService auditLogService) {
        this.baseRepository = baseRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<BaseDTO> getAllBases() {
        return baseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BaseDTO getBaseById(Long id) {
        Base base = baseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with ID: " + id));
        return mapToDTO(base);
    }

    @Transactional
    public BaseDTO createBase(BaseDTO dto, String username) {
        if (baseRepository.existsByBaseCode(dto.getBaseCode())) {
            throw new DuplicateResourceException("Base code '" + dto.getBaseCode() + "' already exists");
        }

        Base base = Base.builder()
                .baseCode(dto.getBaseCode().toUpperCase().trim())
                .baseName(dto.getBaseName().trim())
                .location(dto.getLocation())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        Base saved = baseRepository.save(base);
        auditLogService.log(username, "CREATE", "BASE", saved.getId(), "Created military base: " + saved.getBaseName());
        return mapToDTO(saved);
    }

    @Transactional
    public BaseDTO updateBase(Long id, BaseDTO dto, String username) {
        Base base = baseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with ID: " + id));

        if (!base.getBaseCode().equalsIgnoreCase(dto.getBaseCode()) &&
                baseRepository.existsByBaseCode(dto.getBaseCode())) {
            throw new DuplicateResourceException("Base code '" + dto.getBaseCode() + "' already exists");
        }

        base.setBaseCode(dto.getBaseCode().toUpperCase().trim());
        base.setBaseName(dto.getBaseName().trim());
        base.setLocation(dto.getLocation());
        if (dto.getStatus() != null) {
            base.setStatus(dto.getStatus());
        }

        Base updated = baseRepository.save(base);
        auditLogService.log(username, "UPDATE", "BASE", updated.getId(), "Updated military base: " + updated.getBaseName());
        return mapToDTO(updated);
    }

    @Transactional
    public void deleteBase(Long id, String username) {
        Base base = baseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with ID: " + id));

        baseRepository.delete(base);
        auditLogService.log(username, "DELETE", "BASE", id, "Deleted military base: " + base.getBaseName());
    }

    public BaseDTO mapToDTO(Base base) {
        return BaseDTO.builder()
                .id(base.getId())
                .baseCode(base.getBaseCode())
                .baseName(base.getBaseName())
                .location(base.getLocation())
                .status(base.getStatus())
                .createdAt(base.getCreatedAt())
                .build();
    }
}
