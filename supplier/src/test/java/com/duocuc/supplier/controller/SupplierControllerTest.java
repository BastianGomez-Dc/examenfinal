package com.duocuc.supplier.controller;

import com.duocuc.supplier.dto.SupplierRequest;
import com.duocuc.supplier.dto.SupplierResponse;
import com.duocuc.supplier.service.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplierControllerTest {

    @Mock
    private SupplierService supplierService;

    @InjectMocks
    private SupplierController supplierController;

    private SupplierResponse supplierResponse;

    @BeforeEach
    void setUp() {
        supplierResponse = new SupplierResponse(
                1L, "TecnoParts Distribution", "contact@tecnoparts.cl", "+56221111111", "Av. Industrial 100", true, LocalDate.now());
    }

    @Test
    @DisplayName("create: returns 201 with the created supplier")
    void create_shouldReturn201() {
        // Given
        SupplierRequest request = new SupplierRequest("TecnoParts Distribution", "contact@tecnoparts.cl", "+56221111111", null);
        when(supplierService.save(any(SupplierRequest.class))).thenReturn(supplierResponse);

        // When
        ResponseEntity<SupplierResponse> result = supplierController.create(request);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("TecnoParts Distribution", result.getBody().name());
    }

    @Test
    @DisplayName("findAll: returns 200 with the list of suppliers")
    void findAll_shouldReturn200() {
        // Given
        when(supplierService.findAll()).thenReturn(List.of(supplierResponse));

        // When
        ResponseEntity<List<SupplierResponse>> result = supplierController.findAll();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    @DisplayName("findById: returns 200 with the found supplier")
    void findById_shouldReturn200() {
        // Given
        when(supplierService.findById(1L)).thenReturn(supplierResponse);

        // When
        ResponseEntity<SupplierResponse> result = supplierController.findById(1L);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().id());
    }

    @Test
    @DisplayName("deactivate: returns 204 with no content")
    void deactivate_shouldReturn204() {
        // When
        ResponseEntity<Void> result = supplierController.deactivate(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
