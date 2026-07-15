package com.duocuc.equipment.service.impl;

import com.duocuc.equipment.client.ClientClient;
import com.duocuc.equipment.service.ClientService;
import org.springframework.stereotype.Service;

// Wraps ClientClient: the rest of the service layer never depends on the HTTP client directly.
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientClient clientClient;

    public ClientServiceImpl(ClientClient clientClient) {
        this.clientClient = clientClient;
    }

    @Override
    public boolean existsClient(Long clientId) {
        return clientClient.existsClient(clientId);
    }
}
