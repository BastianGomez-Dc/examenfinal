package com.duocuc.order.dto;

import com.duocuc.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull(message = "The new status is required")
        OrderStatus newStatus
) {}
