package com.duocuc.client.service;

import com.duocuc.client.dto.ClientRequest;
import com.duocuc.client.dto.ClientResponse;
import com.duocuc.client.entity.Client;
import com.duocuc.client.exception.BusinessRuleException;
import com.duocuc.client.exception.DuplicateResourceException;
import com.duocuc.client.exception.ResourceNotFoundException;
import com.duocuc.client.repository.ClientRepository;
import com.duocuc.client.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
 * Unit tests for the client service.
 * Mockito mocks the repository (never touches the real database)
 * and every test follows the Given-When-Then convention.
 */
@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setName("Ana Torres");
        client.setEmail("ana@mail.cl");
        client.setPhone("+56911111111");
        client.setActive(true);
    }

    @Test
    @DisplayName("save: creates a client when the email does not exist")
    void save_shouldCreateClient_whenEmailDoesNotExist() {
        // Given
        ClientRequest request = new ClientRequest("Ana Torres", "ana@mail.cl", "+56911111111", null);
        when(clientRepository.existsByEmail("ana@mail.cl")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        // When
        ClientResponse response = clientService.save(request);

        // Then
        assertEquals("Ana Torres", response.name());
        assertEquals("ana@mail.cl", response.email());
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("save: throws DuplicateResourceException when the email already exists")
    void save_shouldThrowException_whenEmailIsDuplicated() {
        // Given
        ClientRequest request = new ClientRequest("Ana Torres", "ana@mail.cl", null, null);
        when(clientRepository.existsByEmail("ana@mail.cl")).thenReturn(true);

        // When / Then
        assertThrows(DuplicateResourceException.class, () -> clientService.save(request));
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("findById: returns the client when it exists")
    void findById_shouldReturnClient_whenExists() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        // When
        ClientResponse response = clientService.findById(1L);

        // Then
        assertEquals(1L, response.id());
        assertEquals("ana@mail.cl", response.email());
    }

    @Test
    @DisplayName("findById: throws ResourceNotFoundException when it does not exist")
    void findById_shouldThrowException_whenNotFound() {
        // Given
        when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> clientService.findById(99L));
    }

    @Test
    @DisplayName("findAll: returns the list of clients")
    void findAll_shouldReturnList() {
        // Given
        when(clientRepository.findAll()).thenReturn(List.of(client));

        // When
        List<ClientResponse> list = clientService.findAll();

        // Then
        assertEquals(1, list.size());
        assertEquals("Ana Torres", list.get(0).name());
    }

    @Test
    @DisplayName("update: throws BusinessRuleException when the client is inactive")
    void update_shouldThrowException_whenClientIsInactive() {
        // Given
        client.setActive(false);
        ClientRequest request = new ClientRequest("New Name", "ana@mail.cl", null, null);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        // When / Then
        assertThrows(BusinessRuleException.class, () -> clientService.update(1L, request));
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("update: updates the data when the client is active")
    void update_shouldUpdate_whenClientIsActive() {
        // Given
        ClientRequest request = new ClientRequest("Ana Updated", "ana@mail.cl", "+56900000000", "New address 123");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        ClientResponse response = clientService.update(1L, request);

        // Then
        assertEquals("Ana Updated", response.name());
        assertEquals("New address 123", response.address());
    }

    @Test
    @DisplayName("deactivate: marks the client as inactive (logical delete)")
    void deactivate_shouldDeactivateClient() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        clientService.deactivate(1L);

        // Then
        assertFalse(client.getActive());
        verify(clientRepository).save(client);
    }
}
