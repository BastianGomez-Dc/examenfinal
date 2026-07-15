package com.duocuc.msagenda.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CitaRequest(
        @NotNull(message = "El id del cliente es obligatorio")
        @Positive(message = "El id del cliente debe ser positivo")
        Long clienteId,

        @NotNull(message = "El id del técnico es obligatorio")
        @Positive(message = "El id del técnico debe ser positivo")
        Long tecnicoId,

        @NotNull(message = "La fecha y hora de la cita son obligatorias")
        LocalDateTime fechaHora,

        @Size(max = 200, message = "El motivo no puede superar 200 caracteres")
        String motivo
) {}
