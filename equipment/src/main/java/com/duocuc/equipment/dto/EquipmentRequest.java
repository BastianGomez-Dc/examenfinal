package com.duocuc.equipment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EquipmentRequest(
        @NotBlank(message = "Serial number is required")
        @Size(max = 60, message = "Serial number must not exceed 60 characters")
        String serialNumber,

        @NotBlank(message = "Brand is required")
        @Size(max = 60, message = "Brand must not exceed 60 characters")
        String brand,

        @Size(max = 60, message = "Model must not exceed 60 characters")
        String model,

        @NotNull(message = "Client id is required")
        @Positive(message = "Client id must be positive")
        Long clientId,

        @NotNull(message = "Equipment type is required")
        @Positive(message = "Equipment type id must be positive")
        Long equipmentTypeId
) {}
