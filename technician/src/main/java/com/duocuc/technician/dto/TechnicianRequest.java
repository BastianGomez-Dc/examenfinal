package com.duocuc.technician.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TechnicianRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid address")
        @Size(max = 120)
        String email,

        @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Phone must have between 8 and 15 digits")
        String phone,

        @NotNull(message = "Specialty is required")
        @Positive(message = "Specialty id must be positive")
        Long specialtyId
) {}
