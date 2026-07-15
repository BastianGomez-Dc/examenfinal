package com.duocuc.technician.dto;

import java.time.LocalDate;

public record TechnicianResponse(
        Long id,
        String name,
        String email,
        String phone,
        Long specialtyId,
        String specialtyName,
        Boolean active,
        LocalDate hireDate
) {}
