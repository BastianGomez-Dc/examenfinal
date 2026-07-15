package com.duocuc.supplier.dto;

import java.time.LocalDate;

public record SupplierResponse(
        Long id,
        String name,
        String email,
        String phone,
        String address,
        Boolean active,
        LocalDate registrationDate
) {}
