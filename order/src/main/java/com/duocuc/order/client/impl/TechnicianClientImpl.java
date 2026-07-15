package com.duocuc.order.client.impl;

import com.duocuc.order.client.TechnicianClient;
import com.duocuc.order.exception.RemoteServiceException;
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
public class TechnicianClientImpl implements TechnicianClient {

    private static final Logger log = LoggerFactory.getLogger(TechnicianClientImpl.class);

    private final RestClient restClient;

    public TechnicianClientImpl(@Value("${clients.technician-service.base-url}") String baseUrl) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(3))
                .withReadTimeout(Duration.ofSeconds(3));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(ClientHttpRequestFactories.get(settings))
                .build();
    }

    @Override
    public boolean existsTechnician(Long technicianId) {
        try {
            restClient.get()
                    .uri("/technicians/{id}", technicianId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound ex) {
            return false;
        } catch (RestClientException ex) {
            log.error("Error communicating with the technician service for technicianId={}: {}", technicianId, ex.getMessage());
            throw new RemoteServiceException("Could not validate the technician: technician service is unavailable");
        }
    }
}
