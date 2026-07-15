package com.duocuc.technician.controller;

import com.duocuc.technician.dto.SpecialtyRequest;
import com.duocuc.technician.dto.SpecialtyResponse;
import com.duocuc.technician.service.SpecialtyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpecialtyControllerTest {

    @Mock
    private SpecialtyService specialtyService;

    @InjectMocks
    private SpecialtyController specialtyController;

    private SpecialtyResponse specialtyResponse;

    @BeforeEach
    void setUp() {
        specialtyResponse = new SpecialtyResponse(1L, "Hardware", "Repair of physical components", true);
    }

    @Test
    @DisplayName("create: returns 201 with the created specialty")
    void create_shouldReturn201() {
        // Given
        SpecialtyRequest request = new SpecialtyRequest("Hardware", "Repair of physical components");
        when(specialtyService.save(any(SpecialtyRequest.class))).thenReturn(specialtyResponse);

        // When
        ResponseEntity<SpecialtyResponse> result = specialtyController.create(request);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Hardware", result.getBody().name());
    }

    @Test
    @DisplayName("findAll: returns 200 with the list of specialties")
    void findAll_shouldReturn200() {
        // Given
        when(specialtyService.findAll()).thenReturn(List.of(specialtyResponse));

        // When
        ResponseEntity<List<SpecialtyResponse>> result = specialtyController.findAll();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    @DisplayName("findById: returns 200 with the found specialty")
    void findById_shouldReturn200() {
        // Given
        when(specialtyService.findById(1L)).thenReturn(specialtyResponse);

        // When
        ResponseEntity<SpecialtyResponse> result = specialtyController.findById(1L);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().id());
    }

    @Test
    @DisplayName("deactivate: returns 204 with no content")
    void deactivate_shouldReturn204() {
        // When
        ResponseEntity<Void> result = specialtyController.deactivate(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
