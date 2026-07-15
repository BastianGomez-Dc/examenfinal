package com.duocuc.order.service.impl;

import com.duocuc.order.client.ClientClient;
import com.duocuc.order.service.ClientService;
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
