package com.duocuc.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record OrderRequest(
        @NotNull(message = "Client id is required")
        @Positive(message = "Client id must be positive")
        Long clientId,

        @NotNull(message = "Equipment id is required")
        @Positive(message = "Equipment id must be positive")
        Long equipmentId,

        @NotNull(message = "Technician id is required")
        @Positive(message = "Technician id must be positive")
        Long technicianId,

        @NotEmpty(message = "The order must have at least one service")
        @Valid
        List<OrderItemRequest> items
) {}
