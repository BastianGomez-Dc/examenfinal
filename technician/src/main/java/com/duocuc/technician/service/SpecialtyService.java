package com.duocuc.technician.service;

import com.duocuc.technician.dto.SpecialtyRequest;
import com.duocuc.technician.dto.SpecialtyResponse;

import java.util.List;

public interface SpecialtyService {

    SpecialtyResponse save(SpecialtyRequest request);

    List<SpecialtyResponse> findAll();

    SpecialtyResponse findById(Long id);

    SpecialtyResponse update(Long id, SpecialtyRequest request);

    void deactivate(Long id);
}
