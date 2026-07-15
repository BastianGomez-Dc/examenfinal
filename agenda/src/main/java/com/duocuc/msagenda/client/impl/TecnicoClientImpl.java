package com.duocuc.msagenda.client.impl;

import com.duocuc.msagenda.client.TecnicoClient;
import com.duocuc.msagenda.exception.RemoteServiceException;
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
public class TecnicoClientImpl implements TecnicoClient {

    private static final Logger log = LoggerFactory.getLogger(TecnicoClientImpl.class);

    private final RestClient restClient;

    public TecnicoClientImpl(@Value("${clients.ms-tecnico.base-url}") String baseUrl) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(3))
                .withReadTimeout(Duration.ofSeconds(3));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(ClientHttpRequestFactories.get(settings))
                .build();
    }

    @Override
    public boolean existeTecnico(Long tecnicoId) {
        try {
            restClient.get()
                    .uri("/tecnicos/{id}", tecnicoId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound ex) {
            return false;
        } catch (RestClientException ex) {
            log.error("Error al comunicarse con ms-tecnico para tecnicoId={}: {}", tecnicoId, ex.getMessage());
            throw new RemoteServiceException("No se pudo validar el técnico: servicio ms-tecnico no disponible");
        }
    }
}
