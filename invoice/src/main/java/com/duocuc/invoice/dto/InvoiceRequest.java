package com.duocuc.invoice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InvoiceRequest(
        @NotNull(message = "The order id is required")
        @Positive(message = "The order id must be positive")
        Long orderId
) {}
