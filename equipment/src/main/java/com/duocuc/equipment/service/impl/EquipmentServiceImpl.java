package com.duocuc.equipment.service.impl;

import com.duocuc.equipment.dto.EquipmentRequest;
import com.duocuc.equipment.dto.EquipmentResponse;
import com.duocuc.equipment.entity.Equipment;
import com.duocuc.equipment.entity.EquipmentType;
import com.duocuc.equipment.exception.BusinessRuleException;
import com.duocuc.equipment.exception.DuplicateResourceException;
import com.duocuc.equipment.exception.ResourceNotFoundException;
import com.duocuc.equipment.repository.EquipmentRepository;
import com.duocuc.equipment.repository.EquipmentTypeRepository;
import com.duocuc.equipment.service.ClientService;
import com.duocuc.equipment.service.EquipmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private static final Logger log = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    private final EquipmentRepository equipmentRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final ClientService clientService;

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository,
                                 EquipmentTypeRepository equipmentTypeRepository,
                                 ClientService clientService) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.clientService = clientService;
    }

    @Override
    public EquipmentResponse save(EquipmentRequest request) {
        // Business rule: the serial number must be unique
        if (equipmentRepository.existsBySerialNumber(request.serialNumber())) {
            throw new DuplicateResourceException("Equipment already exists with serial number: " + request.serialNumber());
        }
        // Business rule: the equipment type must exist
        EquipmentType equipmentType = equipmentTypeRepository.findById(request.equipmentTypeId())
                .orElseThrow(() -> new BusinessRuleException("The specified equipment type does not exist: " + request.equipmentTypeId()));

        // Business rule: the client must exist in the client service (remote validation)
        if (!clientService.existsClient(request.clientId())) {
            throw new BusinessRuleException("The specified client does not exist: " + request.clientId());
        }

        Equipment equipment = new Equipment();
        equipment.setSerialNumber(request.serialNumber());
        equipment.setBrand(request.brand());
        equipment.setModel(request.model());
        equipment.setClientId(request.clientId());
        equipment.setEquipmentType(equipmentType);
        Equipment saved = equipmentRepository.save(equipment);
        log.info("Equipment created with id={} serialNumber={}", saved.getId(), saved.getSerialNumber());
        return toResponse(saved);
    }

    @Override
    public List<EquipmentResponse> findAll() {
        log.info("Listing all equipment");
        return equipmentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public EquipmentResponse findById(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + id));
        return toResponse(equipment);
    }

    @Override
    public EquipmentResponse update(Long id, EquipmentRequest request) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + id));

        // Business rule: inactive equipment cannot be modified
        if (Boolean.FALSE.equals(equipment.getActive())) {
            throw new BusinessRuleException("Cannot modify inactive equipment (id: " + id + ")");
        }
        if (!equipment.getSerialNumber().equals(request.serialNumber())
                && equipmentRepository.existsBySerialNumber(request.serialNumber())) {
            throw new DuplicateResourceException("Equipment already exists with serial number: " + request.serialNumber());
        }
        EquipmentType equipmentType = equipmentTypeRepository.findById(request.equipmentTypeId())
                .orElseThrow(() -> new BusinessRuleException("The specified equipment type does not exist: " + request.equipmentTypeId()));

        if (!equipment.getClientId().equals(request.clientId()) && !clientService.existsClient(request.clientId())) {
            throw new BusinessRuleException("The specified client does not exist: " + request.clientId());
        }

        equipment.setSerialNumber(request.serialNumber());
        equipment.setBrand(request.brand());
        equipment.setModel(request.model());
        equipment.setClientId(request.clientId());
        equipment.setEquipmentType(equipmentType);
        Equipment updated = equipmentRepository.save(equipment);
        log.info("Equipment updated id={}", updated.getId());
        return toResponse(updated);
    }

    @Override
    public void deactivate(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + id));
        // Business rule: deletion is logical (deactivated, never removed)
        equipment.setActive(false);
        equipmentRepository.save(equipment);
        log.info("Equipment deactivated id={}", id);
    }

    private EquipmentResponse toResponse(Equipment e) {
        return new EquipmentResponse(
                e.getId(), e.getSerialNumber(), e.getBrand(), e.getModel(),
                e.getClientId(), e.getEquipmentType().getId(), e.getEquipmentType().getName(),
                e.getActive(), e.getIntakeDate()
        );
    }
}
