package com.duocuc.msagenda.repository;

import com.duocuc.msagenda.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    // Regla de negocio: un tecnico no puede tener dos citas vigentes a la misma hora
    boolean existsByTecnicoIdAndFechaHoraAndActivoTrue(Long tecnicoId, LocalDateTime fechaHora);

    boolean existsByTecnicoIdAndFechaHoraAndActivoTrueAndIdNot(Long tecnicoId, LocalDateTime fechaHora, Long id);
}
