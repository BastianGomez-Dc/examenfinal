package com.duocuc.client.service;

import com.duocuc.client.dto.ClientRequest;
import com.duocuc.client.dto.ClientResponse;

import java.util.List;

public interface ClientService {

    ClientResponse save(ClientRequest request);

    List<ClientResponse> findAll();

    ClientResponse findById(Long id);

    ClientResponse update(Long id, ClientRequest request);

    void deactivate(Long id);
}
