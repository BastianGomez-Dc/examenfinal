package com.duocuc.msagenda.service.impl;

import com.duocuc.msagenda.client.ClienteClient;
import com.duocuc.msagenda.service.ClienteService;
import org.springframework.stereotype.Service;

// Envuelve al ClienteClient: el resto de la capa de servicio nunca depende del client HTTP directamente.
@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteClient clienteClient;

    public ClienteServiceImpl(ClienteClient clienteClient) {
        this.clienteClient = clienteClient;
    }

    @Override
    public boolean existeCliente(Long clienteId) {
        return clienteClient.existeCliente(clienteId);
    }
}
