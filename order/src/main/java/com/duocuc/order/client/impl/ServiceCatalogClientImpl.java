package com.duocuc.order.client.impl;

import com.duocuc.order.client.ServiceCatalogClient;
import com.duocuc.order.client.dto.MaintenanceServiceSummary;
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
import java.util.Optional;

@Component
public class ServiceCatalogClientImpl implements ServiceCatalogClient {

    private static final Logger log = LoggerFactory.getLogger(ServiceCatalogClientImpl.class);

    private final RestClient restClient;

    public ServiceCatalogClientImpl(@Value("${clients.service-catalog.base-url}") String baseUrl) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(3))
                .withReadTimeout(Duration.ofSeconds(3));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(ClientHttpRequestFactories.get(settings))
                .build();
    }

    @Override
    public Optional<MaintenanceServiceSummary> findService(Long serviceId) {
        try {
            MaintenanceServiceSummary service = restClient.get()
                    .uri("/services/{id}", serviceId)
                    .retrieve()
                    .body(MaintenanceServiceSummary.class);
            return Optional.ofNullable(service);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (RestClientException ex) {
            log.error("Error communicating with the service catalog for serviceId={}: {}", serviceId, ex.getMessage());
            throw new RemoteServiceException("Could not validate the service: service catalog is unavailable");
        }
    }
}
