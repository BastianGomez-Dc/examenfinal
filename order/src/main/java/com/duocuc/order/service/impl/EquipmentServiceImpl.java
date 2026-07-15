package com.duocuc.order.service.impl;

import com.duocuc.order.client.EquipmentClient;
import com.duocuc.order.service.EquipmentService;
import org.springframework.stereotype.Service;

// Wraps EquipmentClient: the rest of the service layer never depends on the HTTP client directly.
@Service
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentClient equipmentClient;

    public EquipmentServiceImpl(EquipmentClient equipmentClient) {
        this.equipmentClient = equipmentClient;
    }

    @Override
    public boolean existsEquipment(Long equipmentId) {
        return equipmentClient.existsEquipment(equipmentId);
    }
}
