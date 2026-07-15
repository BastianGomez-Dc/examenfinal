package com.duocuc.equipment.client.impl;

import com.duocuc.equipment.client.ClientClient;
import com.duocuc.equipment.exception.RemoteServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

@Component
public class ClientClientImpl implements ClientClient {

    private static final Logger log = LoggerFactory.getLogger(ClientClientImpl.class);

    private final RestClient restClient;

    public ClientClientImpl(@Value("${clients.client-service.base-url}") String baseUrl) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(3))
                .withReadTimeout(Duration.ofSeconds(3));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(ClientHttpRequestFactories.get(settings))
                .build();
    }

    @Override
    public boolean existsClient(Long clientId) {
        try {
            restClient.get()
                    .uri("/clients/{id}", clientId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound ex) {
            return false;
        } catch (RestClientException ex) {
            log.error("Error communicating with the client service for clientId={}: {}", clientId, ex.getMessage());
            throw new RemoteServiceException("Could not validate the client: client service is unavailable");
        }
    }
}
