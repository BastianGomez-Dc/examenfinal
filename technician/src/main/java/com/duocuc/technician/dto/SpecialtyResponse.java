package com.duocuc.technician.dto;

public record SpecialtyResponse(
        Long id,
        String name,
        String description,
        Boolean active
) {}
