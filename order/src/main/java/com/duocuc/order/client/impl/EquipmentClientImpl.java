package com.duocuc.order.client.impl;

import com.duocuc.order.client.EquipmentClient;
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
public class EquipmentClientImpl implements EquipmentClient {

    private static final Logger log = LoggerFactory.getLogger(EquipmentClientImpl.class);

    private final RestClient restClient;

    public EquipmentClientImpl(@Value("${clients.equipment-service.base-url}") String baseUrl) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(3))
                .withReadTimeout(Duration.ofSeconds(3));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(ClientHttpRequestFactories.get(settings))
                .build();
    }

    @Override
    public boolean existsEquipment(Long equipmentId) {
        try {
            restClient.get()
                    .uri("/equipment/{id}", equipmentId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound ex) {
            return false;
        } catch (RestClientException ex) {
            log.error("Error communicating with the equipment service for equipmentId={}: {}", equipmentId, ex.getMessage());
            throw new RemoteServiceException("Could not validate the equipment: equipment service is unavailable");
        }
    }
}
