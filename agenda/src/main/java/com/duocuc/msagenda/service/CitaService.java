package com.duocuc.msagenda.service;

import com.duocuc.msagenda.dto.CitaRequest;
import com.duocuc.msagenda.dto.CitaResponse;

import java.util.List;

public interface CitaService {

    CitaResponse save(CitaRequest request);

    List<CitaResponse> findAll();

    CitaResponse findById(Long id);

    CitaResponse update(Long id, CitaRequest request);

    void cancelar(Long id);
}
