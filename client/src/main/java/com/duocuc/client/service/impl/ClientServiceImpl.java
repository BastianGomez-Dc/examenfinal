package com.duocuc.client.service.impl;

import com.duocuc.client.dto.ClientRequest;
import com.duocuc.client.dto.ClientResponse;
import com.duocuc.client.entity.Client;
import com.duocuc.client.exception.BusinessRuleException;
import com.duocuc.client.exception.DuplicateResourceException;
import com.duocuc.client.exception.ResourceNotFoundException;
import com.duocuc.client.repository.ClientRepository;
import com.duocuc.client.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientResponse save(ClientRequest request) {
        // Business rule: the email must be unique across the system
        if (clientRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("A client already exists with email: " + request.email());
        }
        Client client = new Client();
        client.setName(request.name());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setAddress(request.address());
        Client saved = clientRepository.save(client);
        log.info("Client created with id={} email={}", saved.getId(), saved.getEmail());
        return toResponse(saved);
    }

    @Override
    public List<ClientResponse> findAll() {
        log.info("Listing all clients");
        return clientRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ClientResponse findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        return toResponse(client);
    }

    @Override
    public ClientResponse update(Long id, ClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        // Business rule: an inactive client cannot be modified
        if (Boolean.FALSE.equals(client.getActive())) {
            throw new BusinessRuleException("Cannot modify an inactive client (id: " + id + ")");
        }
        // Business rule: if the email changes, the new one must not already be in use
        if (!client.getEmail().equals(request.email()) && clientRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("A client already exists with email: " + request.email());
        }
        client.setName(request.name());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setAddress(request.address());
        Client updated = clientRepository.save(client);
        log.info("Client updated id={}", updated.getId());
        return toResponse(updated);
    }

    @Override
    public void deactivate(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        // Business rule: deletion is logical (deactivated, never removed)
        client.setActive(false);
        clientRepository.save(client);
        log.info("Client deactivated id={}", id);
    }

    private ClientResponse toResponse(Client c) {
        return new ClientResponse(
                c.getId(), c.getName(), c.getEmail(),
                c.getPhone(), c.getAddress(), c.getActive(), c.getRegistrationDate()
        );
    }
}
