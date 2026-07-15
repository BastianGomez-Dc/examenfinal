package com.duocuc.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 60, message = "Name must not exceed 60 characters")
        String name,

        @Size(max = 200, message = "Description must not exceed 200 characters")
        String description
) {}
