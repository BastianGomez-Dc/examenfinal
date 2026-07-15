package com.duocuc.technician.service;

import com.duocuc.technician.dto.SpecialtyRequest;
import com.duocuc.technician.dto.SpecialtyResponse;
import com.duocuc.technician.entity.Specialty;
import com.duocuc.technician.exception.BusinessRuleException;
import com.duocuc.technician.exception.DuplicateResourceException;
import com.duocuc.technician.exception.ResourceNotFoundException;
import com.duocuc.technician.repository.SpecialtyRepository;
import com.duocuc.technician.service.impl.SpecialtyServiceImpl;
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
class SpecialtyServiceImplTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private SpecialtyServiceImpl specialtyService;

    private Specialty specialty;

    @BeforeEach
    void setUp() {
        specialty = new Specialty();
        specialty.setId(1L);
        specialty.setName("Hardware");
        specialty.setDescription("Repair of physical components");
        specialty.setActive(true);
    }

    @Test
    @DisplayName("save: creates a specialty when the name does not exist")
    void save_shouldCreate_whenNameDoesNotExist() {
        // Given
        SpecialtyRequest request = new SpecialtyRequest("Hardware", "Repair of physical components");
        when(specialtyRepository.existsByNameIgnoreCase("Hardware")).thenReturn(false);
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(specialty);

        // When
        SpecialtyResponse response = specialtyService.save(request);

        // Then
        assertEquals("Hardware", response.name());
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    @DisplayName("save: throws DuplicateResourceException when the name already exists")
    void save_shouldThrowException_whenNameIsDuplicated() {
        // Given
        SpecialtyRequest request = new SpecialtyRequest("Hardware", null);
        when(specialtyRepository.existsByNameIgnoreCase("Hardware")).thenReturn(true);

        // When / Then
        assertThrows(DuplicateResourceException.class, () -> specialtyService.save(request));
        verify(specialtyRepository, never()).save(any(Specialty.class));
    }

    @Test
    @DisplayName("findById: throws ResourceNotFoundException when it does not exist")
    void findById_shouldThrowException_whenNotFound() {
        // Given
        when(specialtyRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> specialtyService.findById(99L));
    }

    @Test
    @DisplayName("findAll: returns the list of specialties")
    void findAll_shouldReturnList() {
        // Given
        when(specialtyRepository.findAll()).thenReturn(List.of(specialty));

        // When
        List<SpecialtyResponse> list = specialtyService.findAll();

        // Then
        assertEquals(1, list.size());
        assertEquals("Hardware", list.get(0).name());
    }

    @Test
    @DisplayName("update: throws BusinessRuleException when the specialty is inactive")
    void update_shouldThrowException_whenInactive() {
        // Given
        specialty.setActive(false);
        SpecialtyRequest request = new SpecialtyRequest("Advanced Hardware", null);
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));

        // When / Then
        assertThrows(BusinessRuleException.class, () -> specialtyService.update(1L, request));
        verify(specialtyRepository, never()).save(any(Specialty.class));
    }

    @Test
    @DisplayName("deactivate: marks the specialty as inactive (logical delete)")
    void deactivate_shouldDeactivate() {
        // Given
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));
        when(specialtyRepository.save(any(Specialty.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        specialtyService.deactivate(1L);

        // Then
        assertFalse(specialty.getActive());
        verify(specialtyRepository).save(specialty);
    }
}
