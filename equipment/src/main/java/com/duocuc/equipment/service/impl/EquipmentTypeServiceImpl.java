package com.duocuc.equipment.service.impl;

import com.duocuc.equipment.dto.EquipmentTypeRequest;
import com.duocuc.equipment.dto.EquipmentTypeResponse;
import com.duocuc.equipment.entity.EquipmentType;
import com.duocuc.equipment.exception.BusinessRuleException;
import com.duocuc.equipment.exception.DuplicateResourceException;
import com.duocuc.equipment.exception.ResourceNotFoundException;
import com.duocuc.equipment.repository.EquipmentTypeRepository;
import com.duocuc.equipment.service.EquipmentTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentTypeServiceImpl implements EquipmentTypeService {

    private static final Logger log = LoggerFactory.getLogger(EquipmentTypeServiceImpl.class);

    private final EquipmentTypeRepository equipmentTypeRepository;

    public EquipmentTypeServiceImpl(EquipmentTypeRepository equipmentTypeRepository) {
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    @Override
    public EquipmentTypeResponse save(EquipmentTypeRequest request) {
        // Business rule: the equipment type name must be unique
        if (equipmentTypeRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("An equipment type already exists with name: " + request.name());
        }
        EquipmentType equipmentType = new EquipmentType();
        equipmentType.setName(request.name());
        equipmentType.setDescription(request.description());
        EquipmentType saved = equipmentTypeRepository.save(equipmentType);
        log.info("Equipment type created with id={} name={}", saved.getId(), saved.getName());
        return toResponse(saved);
    }

    @Override
    public List<EquipmentTypeResponse> findAll() {
        log.info("Listing all equipment types");
        return equipmentTypeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public EquipmentTypeResponse findById(Long id) {
        EquipmentType equipmentType = equipmentTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment type not found with id: " + id));
        return toResponse(equipmentType);
    }

    @Override
    public EquipmentTypeResponse update(Long id, EquipmentTypeRequest request) {
        EquipmentType equipmentType = equipmentTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment type not found with id: " + id));

        // Business rule: an inactive equipment type cannot be modified
        if (Boolean.FALSE.equals(equipmentType.getActive())) {
            throw new BusinessRuleException("Cannot modify an inactive equipment type (id: " + id + ")");
        }
        if (!equipmentType.getName().equalsIgnoreCase(request.name())
                && equipmentTypeRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("An equipment type already exists with name: " + request.name());
        }
        equipmentType.setName(request.name());
        equipmentType.setDescription(request.description());
        EquipmentType updated = equipmentTypeRepository.save(equipmentType);
        log.info("Equipment type updated id={}", updated.getId());
        return toResponse(updated);
    }

    @Override
    public void deactivate(Long id) {
        EquipmentType equipmentType = equipmentTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment type not found with id: " + id));
        equipmentType.setActive(false);
        equipmentTypeRepository.save(equipmentType);
        log.info("Equipment type deactivated id={}", id);
    }

    private EquipmentTypeResponse toResponse(EquipmentType t) {
        return new EquipmentTypeResponse(t.getId(), t.getName(), t.getDescription(), t.getActive());
    }
}
