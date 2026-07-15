package com.duocuc.msagenda.controller;

import com.duocuc.msagenda.dto.CitaRequest;
import com.duocuc.msagenda.dto.CitaResponse;
import com.duocuc.msagenda.service.CitaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitaControllerTest {

    @Mock
    private CitaService citaService;

    @InjectMocks
    private CitaController citaController;

    private CitaResponse citaResponse;

    @BeforeEach
    void setUp() {
        citaResponse = new CitaResponse(1L, 1L, 1L, LocalDateTime.of(2026, 9, 1, 10, 0), "Revisión general", true);
    }

    @Test
    @DisplayName("create: retorna 201 con la cita creada")
    void create_deberiaRetornar201() {
        // Given
        CitaRequest request = new CitaRequest(1L, 1L, LocalDateTime.of(2026, 9, 1, 10, 0), "Revisión general");
        when(citaService.save(any(CitaRequest.class))).thenReturn(citaResponse);

        // When
        ResponseEntity<CitaResponse> result = citaController.create(request);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(1L, result.getBody().tecnicoId());
    }

    @Test
    @DisplayName("findAll: retorna 200 con la lista de citas")
    void findAll_deberiaRetornar200() {
        // Given
        when(citaService.findAll()).thenReturn(List.of(citaResponse));

        // When
        ResponseEntity<List<CitaResponse>> result = citaController.findAll();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    @DisplayName("findById: retorna 200 con la cita encontrada")
    void findById_deberiaRetornar200() {
        // Given
        when(citaService.findById(1L)).thenReturn(citaResponse);

        // When
        ResponseEntity<CitaResponse> result = citaController.findById(1L);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().id());
    }

    @Test
    @DisplayName("cancelar: retorna 204 sin contenido")
    void cancelar_deberiaRetornar204() {
        // When
        ResponseEntity<Void> result = citaController.cancelar(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
