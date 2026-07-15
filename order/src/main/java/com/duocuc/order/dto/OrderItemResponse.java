package com.duocuc.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long serviceId,
        String serviceName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal subtotal
) {}
