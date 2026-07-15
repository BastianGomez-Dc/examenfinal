package com.duocuc.technician.service;

import com.duocuc.technician.dto.TechnicianRequest;
import com.duocuc.technician.dto.TechnicianResponse;

import java.util.List;

public interface TechnicianService {

    TechnicianResponse save(TechnicianRequest request);

    List<TechnicianResponse> findAll();

    TechnicianResponse findById(Long id);

    TechnicianResponse update(Long id, TechnicianRequest request);

    void deactivate(Long id);
}
