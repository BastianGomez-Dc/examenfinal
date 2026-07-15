package com.duocuc.part.controller;

import com.duocuc.part.dto.PartRequest;
import com.duocuc.part.dto.PartResponse;
import com.duocuc.part.dto.StockAdjustmentRequest;
import com.duocuc.part.service.PartService;
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
class PartControllerTest {

    @Mock
    private PartService partService;

    @InjectMocks
    private PartController partController;

    private PartResponse partResponse;

    @BeforeEach
    void setUp() {
        partResponse = new PartResponse(
                1L, "480GB SSD", "Solid state drive", new BigDecimal("32000.00"), 25, 1L, true);
    }

    @Test
    @DisplayName("create: returns 201 with the created part")
    void create_shouldReturn201() {
        // Given
        PartRequest request = new PartRequest("480GB SSD", "Solid state drive", new BigDecimal("32000.00"), 25, 1L);
        when(partService.save(any(PartRequest.class))).thenReturn(partResponse);

        // When
        ResponseEntity<PartResponse> result = partController.create(request);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("480GB SSD", result.getBody().name());
    }

    @Test
    @DisplayName("findAll: returns 200 with the list of parts")
    void findAll_shouldReturn200() {
        // Given
        when(partService.findAll()).thenReturn(List.of(partResponse));

        // When
        ResponseEntity<List<PartResponse>> result = partController.findAll();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    @DisplayName("findById: returns 200 with the found part")
    void findById_shouldReturn200() {
        // Given
        when(partService.findById(1L)).thenReturn(partResponse);

        // When
        ResponseEntity<PartResponse> result = partController.findById(1L);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().id());
    }

    @Test
    @DisplayName("subtractStock: returns 200 with the updated stock")
    void subtractStock_shouldReturn200() {
        // Given
        StockAdjustmentRequest request = new StockAdjustmentRequest(5);
        PartResponse updated = new PartResponse(1L, "480GB SSD", null, new BigDecimal("32000.00"), 20, 1L, true);
        when(partService.subtractStock(1L, request)).thenReturn(updated);

        // When
        ResponseEntity<PartResponse> result = partController.subtractStock(1L, request);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(20, result.getBody().stock());
    }

    @Test
    @DisplayName("deactivate: returns 204 with no content")
    void deactivate_shouldReturn204() {
        // When
        ResponseEntity<Void> result = partController.deactivate(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
