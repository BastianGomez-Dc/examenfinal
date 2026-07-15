package com.duocuc.service.dto;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        Boolean active
) {}
