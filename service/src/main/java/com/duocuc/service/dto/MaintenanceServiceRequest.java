package com.duocuc.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MaintenanceServiceRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @Size(max = 200, message = "Description must not exceed 200 characters")
        String description,

        @NotNull(message = "Price is required")
        BigDecimal price,

        @NotNull(message = "Category is required")
        @Positive(message = "Category id must be positive")
        Long categoryId
) {}
