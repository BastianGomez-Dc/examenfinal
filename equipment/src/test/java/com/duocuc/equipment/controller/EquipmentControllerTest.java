package com.duocuc.equipment.controller;

import com.duocuc.equipment.dto.EquipmentRequest;
import com.duocuc.equipment.dto.EquipmentResponse;
import com.duocuc.equipment.service.EquipmentService;
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
class EquipmentControllerTest {

    @Mock
    private EquipmentService equipmentService;

    @InjectMocks
    private EquipmentController equipmentController;

    private EquipmentResponse equipmentResponse;

    @BeforeEach
    void setUp() {
        equipmentResponse = new EquipmentResponse(
                1L, "SN-0001-NB", "Lenovo", "ThinkPad T14", 1L, 1L, "Laptop", true, LocalDate.now());
    }

    @Test
    @DisplayName("create: returns 201 with the created equipment")
    void create_shouldReturn201() {
        // Given
        EquipmentRequest request = new EquipmentRequest("SN-0001-NB", "Lenovo", "ThinkPad T14", 1L, 1L);
        when(equipmentService.save(any(EquipmentRequest.class))).thenReturn(equipmentResponse);

        // When
        ResponseEntity<EquipmentResponse> result = equipmentController.create(request);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("SN-0001-NB", result.getBody().serialNumber());
    }

    @Test
    @DisplayName("findAll: returns 200 with the list of equipment")
    void findAll_shouldReturn200() {
        // Given
        when(equipmentService.findAll()).thenReturn(List.of(equipmentResponse));

        // When
        ResponseEntity<List<EquipmentResponse>> result = equipmentController.findAll();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    @DisplayName("findById: returns 200 with the found equipment")
    void findById_shouldReturn200() {
        // Given
        when(equipmentService.findById(1L)).thenReturn(equipmentResponse);

        // When
        ResponseEntity<EquipmentResponse> result = equipmentController.findById(1L);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().id());
    }

    @Test
    @DisplayName("deactivate: returns 204 with no content")
    void deactivate_shouldReturn204() {
        // When
        ResponseEntity<Void> result = equipmentController.deactivate(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
