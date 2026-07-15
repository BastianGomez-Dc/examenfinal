package com.duocuc.technician.service;

import com.duocuc.technician.dto.TechnicianRequest;
import com.duocuc.technician.dto.TechnicianResponse;
import com.duocuc.technician.entity.Specialty;
import com.duocuc.technician.entity.Technician;
import com.duocuc.technician.exception.BusinessRuleException;
import com.duocuc.technician.exception.DuplicateResourceException;
import com.duocuc.technician.exception.ResourceNotFoundException;
import com.duocuc.technician.repository.SpecialtyRepository;
import com.duocuc.technician.repository.TechnicianRepository;
import com.duocuc.technician.service.impl.TechnicianServiceImpl;
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

@ExtendWith(MockitoExtension.class)
class TechnicianServiceImplTest {

    @Mock
    private TechnicianRepository technicianRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private TechnicianServiceImpl technicianService;

    private Technician technician;
    private Specialty specialty;

    @BeforeEach
    void setUp() {
        specialty = new Specialty();
        specialty.setId(1L);
        specialty.setName("Hardware");
        specialty.setActive(true);

        technician = new Technician();
        technician.setId(1L);
        technician.setName("Javier Soto");
        technician.setEmail("javier.soto@tecnofix.cl");
        technician.setPhone("+56911112222");
        technician.setSpecialty(specialty);
        technician.setActive(true);
    }

    @Test
    @DisplayName("save: creates a technician when the email is unique and the specialty exists")
    void save_shouldCreateTechnician_whenDataIsValid() {
        // Given
        TechnicianRequest request = new TechnicianRequest("Javier Soto", "javier.soto@tecnofix.cl", "+56911112222", 1L);
        when(technicianRepository.existsByEmail("javier.soto@tecnofix.cl")).thenReturn(false);
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));
        when(technicianRepository.save(any(Technician.class))).thenReturn(technician);

        // When
        TechnicianResponse response = technicianService.save(request);

        // Then
        assertEquals("Javier Soto", response.name());
        assertEquals("Hardware", response.specialtyName());
        verify(technicianRepository).save(any(Technician.class));
    }

    @Test
    @DisplayName("save: throws DuplicateResourceException when the email already exists")
    void save_shouldThrowException_whenEmailIsDuplicated() {
        // Given
        TechnicianRequest request = new TechnicianRequest("Javier Soto", "javier.soto@tecnofix.cl", null, 1L);
        when(technicianRepository.existsByEmail("javier.soto@tecnofix.cl")).thenReturn(true);

        // When / Then
        assertThrows(DuplicateResourceException.class, () -> technicianService.save(request));
        verify(technicianRepository, never()).save(any(Technician.class));
    }

    @Test
    @DisplayName("save: throws BusinessRuleException when the specialty does not exist")
    void save_shouldThrowException_whenSpecialtyDoesNotExist() {
        // Given
        TechnicianRequest request = new TechnicianRequest("Javier Soto", "javier.soto@tecnofix.cl", null, 99L);
        when(technicianRepository.existsByEmail("javier.soto@tecnofix.cl")).thenReturn(false);
        when(specialtyRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(BusinessRuleException.class, () -> technicianService.save(request));
        verify(technicianRepository, never()).save(any(Technician.class));
    }

    @Test
    @DisplayName("findById: throws ResourceNotFoundException when it does not exist")
    void findById_shouldThrowException_whenNotFound() {
        // Given
        when(technicianRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> technicianService.findById(99L));
    }

    @Test
    @DisplayName("update: throws BusinessRuleException when the technician is inactive")
    void update_shouldThrowException_whenTechnicianIsInactive() {
        // Given
        technician.setActive(false);
        TechnicianRequest request = new TechnicianRequest("Javier Soto", "javier.soto@tecnofix.cl", null, 1L);
        when(technicianRepository.findById(1L)).thenReturn(Optional.of(technician));

        // When / Then
        assertThrows(BusinessRuleException.class, () -> technicianService.update(1L, request));
        verify(technicianRepository, never()).save(any(Technician.class));
    }

    @Test
    @DisplayName("deactivate: marks the technician as inactive (logical delete)")
    void deactivate_shouldDeactivateTechnician() {
        // Given
        when(technicianRepository.findById(1L)).thenReturn(Optional.of(technician));
        when(technicianRepository.save(any(Technician.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        technicianService.deactivate(1L);

        // Then
        assertFalse(technician.getActive());
        verify(technicianRepository).save(technician);
    }
}
