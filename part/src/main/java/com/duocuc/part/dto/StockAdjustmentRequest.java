package com.duocuc.part.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StockAdjustmentRequest(
        @NotNull(message = "Quantity is required")
        @Positive(message = "The quantity to subtract must be positive")
        Integer quantity
) {}
