package com.duocuc.equipment.service;

import com.duocuc.equipment.dto.EquipmentRequest;
import com.duocuc.equipment.dto.EquipmentResponse;
import com.duocuc.equipment.entity.Equipment;
import com.duocuc.equipment.entity.EquipmentType;
import com.duocuc.equipment.exception.BusinessRuleException;
import com.duocuc.equipment.exception.DuplicateResourceException;
import com.duocuc.equipment.exception.ResourceNotFoundException;
import com.duocuc.equipment.repository.EquipmentRepository;
import com.duocuc.equipment.repository.EquipmentTypeRepository;
import com.duocuc.equipment.service.impl.EquipmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ClientService (local wrapper around the ClientClient) is mocked because it
 * represents the remote communication with the client service.
 */
@ExtendWith(MockitoExtension.class)
class EquipmentServiceImplTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private EquipmentTypeRepository equipmentTypeRepository;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private EquipmentServiceImpl equipmentService;

    private Equipment equipment;
    private EquipmentType equipmentType;

    @BeforeEach
    void setUp() {
        equipmentType = new EquipmentType();
        equipmentType.setId(1L);
        equipmentType.setName("Laptop");
        equipmentType.setActive(true);

        equipment = new Equipment();
        equipment.setId(1L);
        equipment.setSerialNumber("SN-0001-NB");
        equipment.setBrand("Lenovo");
        equipment.setModel("ThinkPad T14");
        equipment.setClientId(1L);
        equipment.setEquipmentType(equipmentType);
        equipment.setActive(true);
    }

    @Test
    @DisplayName("save: creates equipment when the serial number is unique, the type exists and the client exists")
    void save_shouldCreateEquipment_whenDataIsValid() {
        // Given
        EquipmentRequest request = new EquipmentRequest("SN-0001-NB", "Lenovo", "ThinkPad T14", 1L, 1L);
        when(equipmentRepository.existsBySerialNumber("SN-0001-NB")).thenReturn(false);
        when(equipmentTypeRepository.findById(1L)).thenReturn(Optional.of(equipmentType));
        when(clientService.existsClient(1L)).thenReturn(true);
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(equipment);

        // When
        EquipmentResponse response = equipmentService.save(request);

        // Then
        assertEquals("SN-0001-NB", response.serialNumber());
        assertEquals("Laptop", response.equipmentTypeName());
        verify(equipmentRepository).save(any(Equipment.class));
    }

    @Test
    @DisplayName("save: throws DuplicateResourceException when the serial number already exists")
    void save_shouldThrowException_whenSerialNumberIsDuplicated() {
        // Given
        EquipmentRequest request = new EquipmentRequest("SN-0001-NB", "Lenovo", "ThinkPad T14", 1L, 1L);
        when(equipmentRepository.existsBySerialNumber("SN-0001-NB")).thenReturn(true);

        // When / Then
        assertThrows(DuplicateResourceException.class, () -> equipmentService.save(request));
        verify(equipmentRepository, never()).save(any(Equipment.class));
    }

    @Test
    @DisplayName("save: throws BusinessRuleException when the equipment type does not exist")
    void save_shouldThrowException_whenEquipmentTypeDoesNotExist() {
        // Given
        EquipmentRequest request = new EquipmentRequest("SN-0001-NB", "Lenovo", "ThinkPad T14", 1L, 99L);
        when(equipmentRepository.existsBySerialNumber("SN-0001-NB")).thenReturn(false);
        when(equipmentTypeRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(BusinessRuleException.class, () -> equipmentService.save(request));
        verify(equipmentRepository, never()).save(any(Equipment.class));
    }

    @Test
    @DisplayName("save: throws BusinessRuleException when the client does not exist in the client service")
    void save_shouldThrowException_whenClientDoesNotExist() {
        // Given
        EquipmentRequest request = new EquipmentRequest("SN-0001-NB", "Lenovo", "ThinkPad T14", 999L, 1L);
        when(equipmentRepository.existsBySerialNumber("SN-0001-NB")).thenReturn(false);
        when(equipmentTypeRepository.findById(1L)).thenReturn(Optional.of(equipmentType));
        when(clientService.existsClient(999L)).thenReturn(false);

        // When / Then
        assertThrows(BusinessRuleException.class, () -> equipmentService.save(request));
        verify(equipmentRepository, never()).save(any(Equipment.class));
    }

    @Test
    @DisplayName("findById: throws ResourceNotFoundException when it does not exist")
    void findById_shouldThrowException_whenNotFound() {
        // Given
        when(equipmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> equipmentService.findById(99L));
    }

    @Test
    @DisplayName("update: throws BusinessRuleException when equipment is inactive")
    void update_shouldThrowException_whenEquipmentIsInactive() {
        // Given
        equipment.setActive(false);
        EquipmentRequest request = new EquipmentRequest("SN-0001-NB", "Lenovo", "ThinkPad T14", 1L, 1L);
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        // When / Then
        assertThrows(BusinessRuleException.class, () -> equipmentService.update(1L, request));
        verify(equipmentRepository, never()).save(any(Equipment.class));
    }

    @Test
    @DisplayName("deactivate: marks equipment as inactive (logical delete)")
    void deactivate_shouldDeactivateEquipment() {
        // Given
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        equipmentService.deactivate(1L);

        // Then
        assertFalse(equipment.getActive());
        verify(equipmentRepository).save(equipment);
    }
}
