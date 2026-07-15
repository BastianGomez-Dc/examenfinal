package com.duocuc.equipment.service;

import com.duocuc.equipment.dto.EquipmentTypeRequest;
import com.duocuc.equipment.dto.EquipmentTypeResponse;
import com.duocuc.equipment.entity.EquipmentType;
import com.duocuc.equipment.exception.BusinessRuleException;
import com.duocuc.equipment.exception.DuplicateResourceException;
import com.duocuc.equipment.exception.ResourceNotFoundException;
import com.duocuc.equipment.repository.EquipmentTypeRepository;
import com.duocuc.equipment.service.impl.EquipmentTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquipmentTypeServiceImplTest {

    @Mock
    private EquipmentTypeRepository equipmentTypeRepository;

    @InjectMocks
    private EquipmentTypeServiceImpl equipmentTypeService;

    private EquipmentType equipmentType;

    @BeforeEach
    void setUp() {
        equipmentType = new EquipmentType();
        equipmentType.setId(1L);
        equipmentType.setName("Laptop");
        equipmentType.setDescription("Portable computer");
        equipmentType.setActive(true);
    }

    @Test
    @DisplayName("save: creates an equipment type when the name does not exist")
    void save_shouldCreate_whenNameDoesNotExist() {
        // Given
        EquipmentTypeRequest request = new EquipmentTypeRequest("Laptop", "Portable computer");
        when(equipmentTypeRepository.existsByNameIgnoreCase("Laptop")).thenReturn(false);
        when(equipmentTypeRepository.save(any(EquipmentType.class))).thenReturn(equipmentType);

        // When
        EquipmentTypeResponse response = equipmentTypeService.save(request);

        // Then
        assertEquals("Laptop", response.name());
        verify(equipmentTypeRepository).save(any(EquipmentType.class));
    }

    @Test
    @DisplayName("save: throws DuplicateResourceException when the name already exists")
    void save_shouldThrowException_whenNameIsDuplicated() {
        // Given
        EquipmentTypeRequest request = new EquipmentTypeRequest("Laptop", null);
        when(equipmentTypeRepository.existsByNameIgnoreCase("Laptop")).thenReturn(true);

        // When / Then
        assertThrows(DuplicateResourceException.class, () -> equipmentTypeService.save(request));
        verify(equipmentTypeRepository, never()).save(any(EquipmentType.class));
    }

    @Test
    @DisplayName("findById: throws ResourceNotFoundException when it does not exist")
    void findById_shouldThrowException_whenNotFound() {
        // Given
        when(equipmentTypeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> equipmentTypeService.findById(99L));
    }

    @Test
    @DisplayName("findAll: returns the list of equipment types")
    void findAll_shouldReturnList() {
        // Given
        when(equipmentTypeRepository.findAll()).thenReturn(List.of(equipmentType));

        // When
        List<EquipmentTypeResponse> list = equipmentTypeService.findAll();

        // Then
        assertEquals(1, list.size());
        assertEquals("Laptop", list.get(0).name());
    }

    @Test
    @DisplayName("update: throws BusinessRuleException when the equipment type is inactive")
    void update_shouldThrowException_whenInactive() {
        // Given
        equipmentType.setActive(false);
        EquipmentTypeRequest request = new EquipmentTypeRequest("Gaming Laptop", null);
        when(equipmentTypeRepository.findById(1L)).thenReturn(Optional.of(equipmentType));

        // When / Then
        assertThrows(BusinessRuleException.class, () -> equipmentTypeService.update(1L, request));
        verify(equipmentTypeRepository, never()).save(any(EquipmentType.class));
    }

    @Test
    @DisplayName("deactivate: marks the equipment type as inactive (logical delete)")
    void deactivate_shouldDeactivate() {
        // Given
        when(equipmentTypeRepository.findById(1L)).thenReturn(Optional.of(equipmentType));
        when(equipmentTypeRepository.save(any(EquipmentType.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        equipmentTypeService.deactivate(1L);

        // Then
        assertFalse(equipmentType.getActive());
        verify(equipmentTypeRepository).save(equipmentType);
    }
}
