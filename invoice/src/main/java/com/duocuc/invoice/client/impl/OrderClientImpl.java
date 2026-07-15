package com.duocuc.invoice.client.impl;

import com.duocuc.invoice.client.OrderClient;
import com.duocuc.invoice.client.dto.RemoteOrder;
import com.duocuc.invoice.exception.RemoteServiceException;
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
public class OrderClientImpl implements OrderClient {

    private static final Logger log = LoggerFactory.getLogger(OrderClientImpl.class);

    private final RestClient restClient;

    public OrderClientImpl(@Value("${clients.order-service.base-url}") String baseUrl) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(3))
                .withReadTimeout(Duration.ofSeconds(3));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(ClientHttpRequestFactories.get(settings))
                .build();
    }

    @Override
    public Optional<RemoteOrder> findOrder(Long orderId) {
        try {
            RemoteOrder order = restClient.get()
                    .uri("/orders/{id}", orderId)
                    .retrieve()
                    .body(RemoteOrder.class);
            return Optional.ofNullable(order);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (RestClientException ex) {
            log.error("Error communicating with the order service for orderId={}: {}", orderId, ex.getMessage());
            throw new RemoteServiceException("Could not validate the order: order service is unavailable");
        }
    }
}
