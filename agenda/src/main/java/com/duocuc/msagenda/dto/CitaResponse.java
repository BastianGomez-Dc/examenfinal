package com.duocuc.msagenda.dto;

import java.time.LocalDateTime;

public record CitaResponse(
        Long id,
        Long clienteId,
        Long tecnicoId,
        LocalDateTime fechaHora,
        String motivo,
        Boolean activo
) {}
