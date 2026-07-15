package com.duocuc.msagenda.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CitaRepositoryTest {

    @Autowired
    private CitaRepository citaRepository;

    @Test
    @DisplayName("existsByTecnicoIdAndFechaHoraAndActivoTrue: retorna true cuando ya hay una cita a esa hora en la semilla")
    void existsByTecnicoIdAndFechaHoraAndActivoTrue_deberiaRetornarTrue_cuandoExisteEnSemilla() {
        LocalDateTime fechaHora = LocalDateTime.of(2026, 8, 1, 9, 0);
        assertTrue(citaRepository.existsByTecnicoIdAndFechaHoraAndActivoTrue(1L, fechaHora));
    }

    @Test
    @DisplayName("existsByTecnicoIdAndFechaHoraAndActivoTrue: retorna false cuando no hay conflicto de horario")
    void existsByTecnicoIdAndFechaHoraAndActivoTrue_deberiaRetornarFalse_cuandoNoHayConflicto() {
        LocalDateTime fechaHora = LocalDateTime.of(2026, 9, 1, 10, 0);
        assertFalse(citaRepository.existsByTecnicoIdAndFechaHoraAndActivoTrue(1L, fechaHora));
    }

    @Test
    @DisplayName("existsByTecnicoIdAndFechaHoraAndActivoTrueAndIdNot: excluye la propia cita al reagendar")
    void existsByTecnicoIdAndFechaHoraAndActivoTrueAndIdNot_deberiaExcluirLaMismaCita() {
        LocalDateTime fechaHora = LocalDateTime.of(2026, 8, 1, 9, 0);
        assertFalse(citaRepository.existsByTecnicoIdAndFechaHoraAndActivoTrueAndIdNot(1L, fechaHora, 1L));
    }

    @Test
    @DisplayName("findAll: retorna las 5 citas cargadas por la migración semilla")
    void findAll_deberiaRetornarCitasSemilla() {
        assertEquals(5, citaRepository.findAll().size());
    }
}
