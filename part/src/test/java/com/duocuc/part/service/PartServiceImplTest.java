package com.duocuc.part.service;

import com.duocuc.part.dto.PartRequest;
import com.duocuc.part.dto.PartResponse;
import com.duocuc.part.dto.StockAdjustmentRequest;
import com.duocuc.part.entity.Part;
import com.duocuc.part.exception.BusinessRuleException;
import com.duocuc.part.exception.ResourceNotFoundException;
import com.duocuc.part.repository.PartRepository;
import com.duocuc.part.service.impl.PartServiceImpl;
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

/**
 * SupplierService (local wrapper around the SupplierClient) is mocked because it
 * represents the remote communication with the supplier service.
 */
@ExtendWith(MockitoExtension.class)
class PartServiceImplTest {

    @Mock
    private PartRepository partRepository;

    @Mock
    private SupplierService supplierService;

    @InjectMocks
    private PartServiceImpl partService;

    private Part part;

    @BeforeEach
    void setUp() {
        part = new Part();
        part.setId(1L);
        part.setName("480GB SSD");
        part.setDescription("Solid state drive");
        part.setPrice(new BigDecimal("32000.00"));
        part.setStock(25);
        part.setSupplierId(1L);
        part.setActive(true);
    }

    @Test
    @DisplayName("save: creates a part when the stock is valid and the supplier exists")
    void save_shouldCreatePart_whenDataIsValid() {
        // Given
        PartRequest request = new PartRequest("480GB SSD", "Solid state drive", new BigDecimal("32000.00"), 25, 1L);
        when(supplierService.existsSupplier(1L)).thenReturn(true);
        when(partRepository.save(any(Part.class))).thenReturn(part);

        // When
        PartResponse response = partService.save(request);

        // Then
        assertEquals("480GB SSD", response.name());
        assertEquals(25, response.stock());
        verify(partRepository).save(any(Part.class));
    }

    @Test
    @DisplayName("save: throws BusinessRuleException when the stock is negative")
    void save_shouldThrowException_whenStockIsNegative() {
        // Given
        PartRequest request = new PartRequest("480GB SSD", null, new BigDecimal("32000.00"), -1, 1L);

        // When / Then
        assertThrows(BusinessRuleException.class, () -> partService.save(request));
        verify(partRepository, never()).save(any(Part.class));
    }

    @Test
    @DisplayName("save: throws BusinessRuleException when the supplier does not exist")
    void save_shouldThrowException_whenSupplierDoesNotExist() {
        // Given
        PartRequest request = new PartRequest("480GB SSD", null, new BigDecimal("32000.00"), 25, 999L);
        when(supplierService.existsSupplier(999L)).thenReturn(false);

        // When / Then
        assertThrows(BusinessRuleException.class, () -> partService.save(request));
        verify(partRepository, never()).save(any(Part.class));
    }

    @Test
    @DisplayName("findById: throws ResourceNotFoundException when it does not exist")
    void findById_shouldThrowException_whenNotFound() {
        // Given
        when(partRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> partService.findById(99L));
    }

    @Test
    @DisplayName("subtractStock: subtracts stock when there is enough availability")
    void subtractStock_shouldSubtract_whenStockIsSufficient() {
        // Given
        StockAdjustmentRequest request = new StockAdjustmentRequest(10);
        when(partRepository.findById(1L)).thenReturn(Optional.of(part));
        when(partRepository.save(any(Part.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        PartResponse response = partService.subtractStock(1L, request);

        // Then
        assertEquals(15, response.stock());
    }

    @Test
    @DisplayName("subtractStock: throws BusinessRuleException when the quantity exceeds the available stock")
    void subtractStock_shouldThrowException_whenStockIsInsufficient() {
        // Given
        StockAdjustmentRequest request = new StockAdjustmentRequest(100);
        when(partRepository.findById(1L)).thenReturn(Optional.of(part));

        // When / Then
        assertThrows(BusinessRuleException.class, () -> partService.subtractStock(1L, request));
        verify(partRepository, never()).save(any(Part.class));
    }

    @Test
    @DisplayName("update: throws BusinessRuleException when the part is inactive")
    void update_shouldThrowException_whenPartIsInactive() {
        // Given
        part.setActive(false);
        PartRequest request = new PartRequest("480GB SSD", null, new BigDecimal("32000.00"), 25, 1L);
        when(partRepository.findById(1L)).thenReturn(Optional.of(part));

        // When / Then
        assertThrows(BusinessRuleException.class, () -> partService.update(1L, request));
        verify(partRepository, never()).save(any(Part.class));
    }

    @Test
    @DisplayName("deactivate: marks the part as inactive (logical delete)")
    void deactivate_shouldDeactivatePart() {
        // Given
        when(partRepository.findById(1L)).thenReturn(Optional.of(part));
        when(partRepository.save(any(Part.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        partService.deactivate(1L);

        // Then
        assertFalse(part.getActive());
        verify(partRepository).save(part);
    }
}
