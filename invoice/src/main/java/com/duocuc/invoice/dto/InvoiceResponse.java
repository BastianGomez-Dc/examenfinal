package com.duocuc.invoice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceResponse(
        Long id,
        Long orderId,
        BigDecimal total,
        LocalDate issueDate
) {}
