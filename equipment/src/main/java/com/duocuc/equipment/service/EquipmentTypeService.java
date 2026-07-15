package com.duocuc.equipment.service;

import com.duocuc.equipment.dto.EquipmentTypeRequest;
import com.duocuc.equipment.dto.EquipmentTypeResponse;

import java.util.List;

public interface EquipmentTypeService {

    EquipmentTypeResponse save(EquipmentTypeRequest request);

    List<EquipmentTypeResponse> findAll();

    EquipmentTypeResponse findById(Long id);

    EquipmentTypeResponse update(Long id, EquipmentTypeRequest request);

    void deactivate(Long id);
}
