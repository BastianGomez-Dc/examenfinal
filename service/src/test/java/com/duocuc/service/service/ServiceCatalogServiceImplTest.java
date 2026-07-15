package com.duocuc.service.service;

import com.duocuc.service.dto.MaintenanceServiceRequest;
import com.duocuc.service.dto.MaintenanceServiceResponse;
import com.duocuc.service.entity.Category;
import com.duocuc.service.entity.MaintenanceService;
import com.duocuc.service.exception.BusinessRuleException;
import com.duocuc.service.exception.DuplicateResourceException;
import com.duocuc.service.exception.ResourceNotFoundException;
import com.duocuc.service.repository.CategoryRepository;
import com.duocuc.service.repository.MaintenanceServiceRepository;
import com.duocuc.service.service.impl.ServiceCatalogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
class ServiceCatalogServiceImplTest {

    @Mock
    private MaintenanceServiceRepository maintenanceServiceRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ServiceCatalogServiceImpl serviceCatalogService;

    private MaintenanceService service;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Diagnostics");
        category.setActive(true);

        service = new MaintenanceService();
        service.setId(1L);
        service.setName("General diagnostics");
        service.setDescription("Full checkup");
        service.setPrice(new BigDecimal("15000.00"));
        service.setCategory(category);
        service.setActive(true);
    }

    @Test
    @DisplayName("save: creates a service when the price is valid, the category exists and the name is unique in the category")
    void save_shouldCreateService_whenDataIsValid() {
        // Given
        MaintenanceServiceRequest request = new MaintenanceServiceRequest("General diagnostics", "Full checkup", new BigDecimal("15000.00"), 1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(maintenanceServiceRepository.existsByNameIgnoreCaseAndCategoryId("General diagnostics", 1L)).thenReturn(false);
        when(maintenanceServiceRepository.save(any(MaintenanceService.class))).thenReturn(service);

        // When
        MaintenanceServiceResponse response = serviceCatalogService.save(request);

        // Then
        assertEquals("General diagnostics", response.name());
        assertEquals("Diagnostics", response.categoryName());
        verify(maintenanceServiceRepository).save(any(MaintenanceService.class));
    }

    @Test
    @DisplayName("save: throws BusinessRuleException when the price is not greater than 0")
    void save_shouldThrowException_whenPriceIsNotPositive() {
        // Given
        MaintenanceServiceRequest request = new MaintenanceServiceRequest("General diagnostics", null, BigDecimal.ZERO, 1L);

        // When / Then
        assertThrows(BusinessRuleException.class, () -> serviceCatalogService.save(request));
        verify(maintenanceServiceRepository, never()).save(any(MaintenanceService.class));
    }

    @Test
    @DisplayName("save: throws BusinessRuleException when the category does not exist")
    void save_shouldThrowException_whenCategoryDoesNotExist() {
        // Given
        MaintenanceServiceRequest request = new MaintenanceServiceRequest("General diagnostics", null, new BigDecimal("15000.00"), 99L);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(BusinessRuleException.class, () -> serviceCatalogService.save(request));
        verify(maintenanceServiceRepository, never()).save(any(MaintenanceService.class));
    }

    @Test
    @DisplayName("save: throws DuplicateResourceException when the name already exists in the same category")
    void save_shouldThrowException_whenNameIsDuplicatedInCategory() {
        // Given
        MaintenanceServiceRequest request = new MaintenanceServiceRequest("General diagnostics", null, new BigDecimal("15000.00"), 1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(maintenanceServiceRepository.existsByNameIgnoreCaseAndCategoryId("General diagnostics", 1L)).thenReturn(true);

        // When / Then
        assertThrows(DuplicateResourceException.class, () -> serviceCatalogService.save(request));
        verify(maintenanceServiceRepository, never()).save(any(MaintenanceService.class));
    }

    @Test
    @DisplayName("findById: throws ResourceNotFoundException when it does not exist")
    void findById_shouldThrowException_whenNotFound() {
        // Given
        when(maintenanceServiceRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> serviceCatalogService.findById(99L));
    }

    @Test
    @DisplayName("update: throws BusinessRuleException when the service is inactive")
    void update_shouldThrowException_whenServiceIsInactive() {
        // Given
        service.setActive(false);
        MaintenanceServiceRequest request = new MaintenanceServiceRequest("General diagnostics", null, new BigDecimal("15000.00"), 1L);
        when(maintenanceServiceRepository.findById(1L)).thenReturn(Optional.of(service));

        // When / Then
        assertThrows(BusinessRuleException.class, () -> serviceCatalogService.update(1L, request));
        verify(maintenanceServiceRepository, never()).save(any(MaintenanceService.class));
    }

    @Test
    @DisplayName("deactivate: marks the service as inactive (logical delete)")
    void deactivate_shouldDeactivateService() {
        // Given
        when(maintenanceServiceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(maintenanceServiceRepository.save(any(MaintenanceService.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        serviceCatalogService.deactivate(1L);

        // Then
        assertFalse(service.getActive());
        verify(maintenanceServiceRepository).save(service);
    }
}
