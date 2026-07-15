package com.duocuc.client.controller;

import com.duocuc.client.dto.ClientRequest;
import com.duocuc.client.dto.ClientResponse;
import com.duocuc.client.service.ClientService;
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
class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private ClientResponse clientResponse;

    @BeforeEach
    void setUp() {
        clientResponse = new ClientResponse(
                1L, "Ana Torres", "ana.torres@mail.cl", "+56911111111", "Av. Providencia 100", true, LocalDate.now());
    }

    @Test
    @DisplayName("create: returns 201 with the created client")
    void create_shouldReturn201() {
        // Given
        ClientRequest request = new ClientRequest("Ana Torres", "ana.torres@mail.cl", "+56911111111", null);
        when(clientService.save(any(ClientRequest.class))).thenReturn(clientResponse);

        // When
        ResponseEntity<ClientResponse> result = clientController.create(request);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Ana Torres", result.getBody().name());
    }

    @Test
    @DisplayName("findAll: returns 200 with the list of clients")
    void findAll_shouldReturn200() {
        // Given
        when(clientService.findAll()).thenReturn(List.of(clientResponse));

        // When
        ResponseEntity<List<ClientResponse>> result = clientController.findAll();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    @DisplayName("findById: returns 200 with the found client")
    void findById_shouldReturn200() {
        // Given
        when(clientService.findById(1L)).thenReturn(clientResponse);

        // When
        ResponseEntity<ClientResponse> result = clientController.findById(1L);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().id());
    }

    @Test
    @DisplayName("update: returns 200 with the updated client")
    void update_shouldReturn200() {
        // Given
        ClientRequest request = new ClientRequest("Ana Updated", "ana.torres@mail.cl", null, null);
        when(clientService.update(any(Long.class), any(ClientRequest.class))).thenReturn(clientResponse);

        // When
        ResponseEntity<ClientResponse> result = clientController.update(1L, request);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("deactivate: returns 204 with no content")
    void deactivate_shouldReturn204() {
        // When
        ResponseEntity<Void> result = clientController.deactivate(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
