package com.duocuc.part.dto;

import java.math.BigDecimal;

public record PartResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        Long supplierId,
        Boolean active
) {}
