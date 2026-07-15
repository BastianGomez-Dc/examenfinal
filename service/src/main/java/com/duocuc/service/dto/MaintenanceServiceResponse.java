package com.duocuc.service.dto;

import java.math.BigDecimal;

public record MaintenanceServiceResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Long categoryId,
        String categoryName,
        Boolean active
) {}
