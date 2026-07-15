package com.duocuc.technician.controller;

import com.duocuc.technician.dto.TechnicianRequest;
import com.duocuc.technician.dto.TechnicianResponse;
import com.duocuc.technician.service.TechnicianService;
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
class TechnicianControllerTest {

    @Mock
    private TechnicianService technicianService;

    @InjectMocks
    private TechnicianController technicianController;

    private TechnicianResponse technicianResponse;

    @BeforeEach
    void setUp() {
        technicianResponse = new TechnicianResponse(
                1L, "Javier Soto", "javier.soto@tecnofix.cl", "+56911112222", 1L, "Hardware", true, LocalDate.now());
    }

    @Test
    @DisplayName("create: returns 201 with the created technician")
    void create_shouldReturn201() {
        // Given
        TechnicianRequest request = new TechnicianRequest("Javier Soto", "javier.soto@tecnofix.cl", "+56911112222", 1L);
        when(technicianService.save(any(TechnicianRequest.class))).thenReturn(technicianResponse);

        // When
        ResponseEntity<TechnicianResponse> result = technicianController.create(request);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Javier Soto", result.getBody().name());
    }

    @Test
    @DisplayName("findAll: returns 200 with the list of technicians")
    void findAll_shouldReturn200() {
        // Given
        when(technicianService.findAll()).thenReturn(List.of(technicianResponse));

        // When
        ResponseEntity<List<TechnicianResponse>> result = technicianController.findAll();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    @DisplayName("findById: returns 200 with the found technician")
    void findById_shouldReturn200() {
        // Given
        when(technicianService.findById(1L)).thenReturn(technicianResponse);

        // When
        ResponseEntity<TechnicianResponse> result = technicianController.findById(1L);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().id());
    }

    @Test
    @DisplayName("deactivate: returns 204 with no content")
    void deactivate_shouldReturn204() {
        // When
        ResponseEntity<Void> result = technicianController.deactivate(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
