package com.duocuc.supplier.service;

import com.duocuc.supplier.dto.SupplierRequest;
import com.duocuc.supplier.dto.SupplierResponse;
import com.duocuc.supplier.entity.Supplier;
import com.duocuc.supplier.exception.BusinessRuleException;
import com.duocuc.supplier.exception.DuplicateResourceException;
import com.duocuc.supplier.exception.ResourceNotFoundException;
import com.duocuc.supplier.repository.SupplierRepository;
import com.duocuc.supplier.service.impl.SupplierServiceImpl;
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
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("TecnoParts Distribution");
        supplier.setEmail("contact@tecnoparts.cl");
        supplier.setPhone("+56221111111");
        supplier.setActive(true);
    }

    @Test
    @DisplayName("save: creates a supplier when the email does not exist")
    void save_shouldCreateSupplier_whenEmailDoesNotExist() {
        // Given
        SupplierRequest request = new SupplierRequest("TecnoParts Distribution", "contact@tecnoparts.cl", "+56221111111", null);
        when(supplierRepository.existsByEmail("contact@tecnoparts.cl")).thenReturn(false);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);

        // When
        SupplierResponse response = supplierService.save(request);

        // Then
        assertEquals("TecnoParts Distribution", response.name());
        assertEquals("contact@tecnoparts.cl", response.email());
        verify(supplierRepository).save(any(Supplier.class));
    }

    @Test
    @DisplayName("save: throws DuplicateResourceException when the email already exists")
    void save_shouldThrowException_whenEmailIsDuplicated() {
        // Given
        SupplierRequest request = new SupplierRequest("TecnoParts Distribution", "contact@tecnoparts.cl", null, null);
        when(supplierRepository.existsByEmail("contact@tecnoparts.cl")).thenReturn(true);

        // When / Then
        assertThrows(DuplicateResourceException.class, () -> supplierService.save(request));
        verify(supplierRepository, never()).save(any(Supplier.class));
    }

    @Test
    @DisplayName("findById: returns the supplier when it exists")
    void findById_shouldReturnSupplier_whenExists() {
        // Given
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        // When
        SupplierResponse response = supplierService.findById(1L);

        // Then
        assertEquals(1L, response.id());
        assertEquals("contact@tecnoparts.cl", response.email());
    }

    @Test
    @DisplayName("findById: throws ResourceNotFoundException when it does not exist")
    void findById_shouldThrowException_whenNotFound() {
        // Given
        when(supplierRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> supplierService.findById(99L));
    }

    @Test
    @DisplayName("findAll: returns the list of suppliers")
    void findAll_shouldReturnList() {
        // Given
        when(supplierRepository.findAll()).thenReturn(List.of(supplier));

        // When
        List<SupplierResponse> list = supplierService.findAll();

        // Then
        assertEquals(1, list.size());
        assertEquals("TecnoParts Distribution", list.get(0).name());
    }

    @Test
    @DisplayName("update: throws BusinessRuleException when the supplier is inactive")
    void update_shouldThrowException_whenSupplierIsInactive() {
        // Given
        supplier.setActive(false);
        SupplierRequest request = new SupplierRequest("New Name", "contact@tecnoparts.cl", null, null);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        // When / Then
        assertThrows(BusinessRuleException.class, () -> supplierService.update(1L, request));
        verify(supplierRepository, never()).save(any(Supplier.class));
    }

    @Test
    @DisplayName("update: updates the data when the supplier is active")
    void update_shouldUpdate_whenSupplierIsActive() {
        // Given
        SupplierRequest request = new SupplierRequest("TecnoParts Updated", "contact@tecnoparts.cl", "+56229999999", "New address 123");
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.save(any(Supplier.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        SupplierResponse response = supplierService.update(1L, request);

        // Then
        assertEquals("TecnoParts Updated", response.name());
        assertEquals("New address 123", response.address());
    }

    @Test
    @DisplayName("deactivate: marks the supplier as inactive (logical delete)")
    void deactivate_shouldDeactivateSupplier() {
        // Given
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.save(any(Supplier.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        supplierService.deactivate(1L);

        // Then
        assertFalse(supplier.getActive());
        verify(supplierRepository).save(supplier);
    }
}
