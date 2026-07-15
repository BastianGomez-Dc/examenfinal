package com.duocuc.msagenda.service.impl;

import com.duocuc.msagenda.client.TecnicoClient;
import com.duocuc.msagenda.service.TecnicoService;
import org.springframework.stereotype.Service;

// Envuelve al TecnicoClient: el resto de la capa de servicio nunca depende del client HTTP directamente.
@Service
public class TecnicoServiceImpl implements TecnicoService {

    private final TecnicoClient tecnicoClient;

    public TecnicoServiceImpl(TecnicoClient tecnicoClient) {
        this.tecnicoClient = tecnicoClient;
    }

    @Override
    public boolean existeTecnico(Long tecnicoId) {
        return tecnicoClient.existeTecnico(tecnicoId);
    }
}
