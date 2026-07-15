package com.duocuc.equipment.dto;

public record EquipmentTypeResponse(
        Long id,
        String name,
        String description,
        Boolean active
) {}
