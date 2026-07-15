package com.duocuc.order.dto;

import com.duocuc.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrderResponse(
        Long id,
        Long clientId,
        Long equipmentId,
        Long technicianId,
        OrderStatus status,
        BigDecimal total,
        LocalDate createdDate,
        List<OrderItemResponse> items
) {}
