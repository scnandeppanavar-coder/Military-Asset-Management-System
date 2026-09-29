package com.mams.service;

import com.mams.dto.EquipmentTypeDTO;
import com.mams.entity.EquipmentType;
import com.mams.exception.DuplicateResourceException;
import com.mams.exception.ResourceNotFoundException;
import com.mams.repository.EquipmentTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipmentTypeService {

    private final EquipmentTypeRepository equipmentTypeRepository;
    private final AuditLogService auditLogService;

    public EquipmentTypeService(EquipmentTypeRepository equipmentTypeRepository, AuditLogService auditLogService) {
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<EquipmentTypeDTO> getAllEquipmentTypes() {
        return equipmentTypeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EquipmentTypeDTO getEquipmentTypeById(Long id) {
        EquipmentType eq = equipmentTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment type not found with ID: " + id));
        return mapToDTO(eq);
    }

    @Transactional
    public EquipmentTypeDTO createEquipmentType(EquipmentTypeDTO dto, String username) {
        if (equipmentTypeRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Equipment type with name '" + dto.getName() + "' already exists");
        }

        EquipmentType equipmentType = EquipmentType.builder()
                .name(dto.getName().trim())
                .category(dto.getCategory().trim())
                .description(dto.getDescription())
                .unit(dto.getUnit().trim())
                .build();

        EquipmentType saved = equipmentTypeRepository.save(equipmentType);
        auditLogService.log(username, "CREATE", "EQUIPMENT_TYPE", saved.getId(), "Created equipment type: " + saved.getName());
        return mapToDTO(saved);
    }

    @Transactional
    public EquipmentTypeDTO updateEquipmentType(Long id, EquipmentTypeDTO dto, String username) {
        EquipmentType eq = equipmentTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment type not found with ID: " + id));

        if (!eq.getName().equalsIgnoreCase(dto.getName()) &&
                equipmentTypeRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Equipment type with name '" + dto.getName() + "' already exists");
        }

        eq.setName(dto.getName().trim());
        eq.setCategory(dto.getCategory().trim());
        eq.setDescription(dto.getDescription());
        eq.setUnit(dto.getUnit().trim());

        EquipmentType updated = equipmentTypeRepository.save(eq);
        auditLogService.log(username, "UPDATE", "EQUIPMENT_TYPE", updated.getId(), "Updated equipment type: " + updated.getName());
        return mapToDTO(updated);
    }

    @Transactional
    public void deleteEquipmentType(Long id, String username) {
        EquipmentType eq = equipmentTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment type not found with ID: " + id));

        equipmentTypeRepository.delete(eq);
        auditLogService.log(username, "DELETE", "EQUIPMENT_TYPE", id, "Deleted equipment type: " + eq.getName());
    }

    public EquipmentTypeDTO mapToDTO(EquipmentType eq) {
        return EquipmentTypeDTO.builder()
                .id(eq.getId())
                .name(eq.getName())
                .category(eq.getCategory())
                .description(eq.getDescription())
                .unit(eq.getUnit())
                .createdAt(eq.getCreatedAt())
                .build();
    }
}
