package com.duocuc.equipment.dto;

import java.time.LocalDate;

public record EquipmentResponse(
        Long id,
        String serialNumber,
        String brand,
        String model,
        Long clientId,
        Long equipmentTypeId,
        String equipmentTypeName,
        Boolean active,
        LocalDate intakeDate
) {}
