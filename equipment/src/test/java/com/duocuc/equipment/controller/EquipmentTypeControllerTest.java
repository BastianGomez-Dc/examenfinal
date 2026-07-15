package com.duocuc.equipment.controller;

import com.duocuc.equipment.dto.EquipmentTypeRequest;
import com.duocuc.equipment.dto.EquipmentTypeResponse;
import com.duocuc.equipment.service.EquipmentTypeService;
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
class EquipmentTypeControllerTest {

    @Mock
    private EquipmentTypeService equipmentTypeService;

    @InjectMocks
    private EquipmentTypeController equipmentTypeController;

    private EquipmentTypeResponse equipmentTypeResponse;

    @BeforeEach
    void setUp() {
        equipmentTypeResponse = new EquipmentTypeResponse(1L, "Laptop", "Portable computer", true);
    }

    @Test
    @DisplayName("create: returns 201 with the created equipment type")
    void create_shouldReturn201() {
        // Given
        EquipmentTypeRequest request = new EquipmentTypeRequest("Laptop", "Portable computer");
        when(equipmentTypeService.save(any(EquipmentTypeRequest.class))).thenReturn(equipmentTypeResponse);

        // When
        ResponseEntity<EquipmentTypeResponse> result = equipmentTypeController.create(request);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Laptop", result.getBody().name());
    }

    @Test
    @DisplayName("findAll: returns 200 with the list of equipment types")
    void findAll_shouldReturn200() {
        // Given
        when(equipmentTypeService.findAll()).thenReturn(List.of(equipmentTypeResponse));

        // When
        ResponseEntity<List<EquipmentTypeResponse>> result = equipmentTypeController.findAll();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    @DisplayName("findById: returns 200 with the found equipment type")
    void findById_shouldReturn200() {
        // Given
        when(equipmentTypeService.findById(1L)).thenReturn(equipmentTypeResponse);

        // When
        ResponseEntity<EquipmentTypeResponse> result = equipmentTypeController.findById(1L);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().id());
    }

    @Test
    @DisplayName("deactivate: returns 204 with no content")
    void deactivate_shouldReturn204() {
        // When
        ResponseEntity<Void> result = equipmentTypeController.deactivate(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
