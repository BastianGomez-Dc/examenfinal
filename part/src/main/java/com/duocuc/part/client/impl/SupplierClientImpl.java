package com.duocuc.part.client.impl;

import com.duocuc.part.client.SupplierClient;
import com.duocuc.part.exception.RemoteServiceException;
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
public class SupplierClientImpl implements SupplierClient {

    private static final Logger log = LoggerFactory.getLogger(SupplierClientImpl.class);

    private final RestClient restClient;

    public SupplierClientImpl(@Value("${clients.supplier-service.base-url}") String baseUrl) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(3))
                .withReadTimeout(Duration.ofSeconds(3));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(ClientHttpRequestFactories.get(settings))
                .build();
    }

    @Override
    public boolean existsSupplier(Long supplierId) {
        try {
            restClient.get()
                    .uri("/suppliers/{id}", supplierId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound ex) {
            return false;
        } catch (RestClientException ex) {
            log.error("Error communicating with the supplier service for supplierId={}: {}", supplierId, ex.getMessage());
            throw new RemoteServiceException("Could not validate the supplier: supplier service is unavailable");
        }
    }
}
