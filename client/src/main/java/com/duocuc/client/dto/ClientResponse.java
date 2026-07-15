package com.duocuc.client.dto;

import java.time.LocalDate;

public record ClientResponse(
        Long id,
        String name,
        String email,
        String phone,
        String address,
        Boolean active,
        LocalDate registrationDate
) {}
