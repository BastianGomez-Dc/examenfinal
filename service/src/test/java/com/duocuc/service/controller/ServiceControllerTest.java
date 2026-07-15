package com.duocuc.service.controller;

import com.duocuc.service.dto.MaintenanceServiceRequest;
import com.duocuc.service.dto.MaintenanceServiceResponse;
import com.duocuc.service.service.ServiceCatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceControllerTest {

    @Mock
    private ServiceCatalogService serviceCatalogService;

    @InjectMocks
    private ServiceController serviceController;

    private MaintenanceServiceResponse serviceResponse;

    @BeforeEach
    void setUp() {
        serviceResponse = new MaintenanceServiceResponse(
                1L, "General diagnostics", "Full checkup", new BigDecimal("15000.00"), 1L, "Diagnostics", true);
    }

    @Test
    @DisplayName("create: returns 201 with the created service")
    void create_shouldReturn201() {
        // Given
        MaintenanceServiceRequest request = new MaintenanceServiceRequest("General diagnostics", "Full checkup", new BigDecimal("15000.00"), 1L);
        when(serviceCatalogService.save(any(MaintenanceServiceRequest.class))).thenReturn(serviceResponse);

        // When
        ResponseEntity<MaintenanceServiceResponse> result = serviceController.create(request);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("General diagnostics", result.getBody().name());
    }

    @Test
    @DisplayName("findAll: returns 200 with the list of services")
    void findAll_shouldReturn200() {
        // Given
        when(serviceCatalogService.findAll()).thenReturn(List.of(serviceResponse));

        // When
        ResponseEntity<List<MaintenanceServiceResponse>> result = serviceController.findAll();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    @DisplayName("findById: returns 200 with the found service")
    void findById_shouldReturn200() {
        // Given
        when(serviceCatalogService.findById(1L)).thenReturn(serviceResponse);

        // When
        ResponseEntity<MaintenanceServiceResponse> result = serviceController.findById(1L);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().id());
    }

    @Test
    @DisplayName("deactivate: returns 204 with no content")
    void deactivate_shouldReturn204() {
        // When
        ResponseEntity<Void> result = serviceController.deactivate(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
