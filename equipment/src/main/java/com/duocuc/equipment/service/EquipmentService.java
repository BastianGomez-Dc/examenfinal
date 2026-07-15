package com.duocuc.equipment.service;

import com.duocuc.equipment.dto.EquipmentRequest;
import com.duocuc.equipment.dto.EquipmentResponse;

import java.util.List;

public interface EquipmentService {

    EquipmentResponse save(EquipmentRequest request);

    List<EquipmentResponse> findAll();

    EquipmentResponse findById(Long id);

    EquipmentResponse update(Long id, EquipmentRequest request);

    void deactivate(Long id);
}
