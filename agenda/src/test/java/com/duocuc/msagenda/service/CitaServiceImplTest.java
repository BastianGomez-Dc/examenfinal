package com.duocuc.msagenda.service;

import com.duocuc.msagenda.dto.CitaRequest;
import com.duocuc.msagenda.dto.CitaResponse;
import com.duocuc.msagenda.entity.Cita;
import com.duocuc.msagenda.exception.BusinessRuleException;
import com.duocuc.msagenda.exception.DuplicateResourceException;
import com.duocuc.msagenda.exception.ResourceNotFoundException;
import com.duocuc.msagenda.repository.CitaRepository;
import com.duocuc.msagenda.service.impl.CitaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ClienteService y TecnicoService (wrappers locales de sus clients) se mockean porque
 * representan la comunicación remota con ms-cliente y ms-tecnico.
 */
@ExtendWith(MockitoExtension.class)
class CitaServiceImplTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private TecnicoService tecnicoService;

    @InjectMocks
    private CitaServiceImpl citaService;

    private Cita cita;
    private LocalDateTime fechaFutura;

    @BeforeEach
    void setUp() {
        fechaFutura = LocalDateTime.now().plusDays(5).withHour(9).withMinute(0).withSecond(0).withNano(0);

        cita = new Cita();
        cita.setId(1L);
        cita.setClienteId(1L);
        cita.setTecnicoId(1L);
        cita.setFechaHora(fechaFutura);
        cita.setMotivo("Revisión general");
        cita.setActivo(true);
    }

    @Test
    @DisplayName("save: crea una cita cuando cliente y técnico existen, la fecha es futura y no hay conflicto de horario")
    void save_deberiaCrearCita_cuandoDatosValidos() {
        // Given
        CitaRequest request = new CitaRequest(1L, 1L, fechaFutura, "Revisión general");
        when(clienteService.existeCliente(1L)).thenReturn(true);
        when(tecnicoService.existeTecnico(1L)).thenReturn(true);
        when(citaRepository.existsByTecnicoIdAndFechaHoraAndActivoTrue(1L, fechaFutura)).thenReturn(false);
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        // When
        CitaResponse response = citaService.save(request);

        // Then
        assertEquals(1L, response.tecnicoId());
        assertEquals(fechaFutura, response.fechaHora());
    }

    @Test
    @DisplayName("save: lanza BusinessRuleException cuando el cliente no existe")
    void save_deberiaLanzarExcepcion_cuandoClienteNoExiste() {
        // Given
        CitaRequest request = new CitaRequest(999L, 1L, fechaFutura, null);
        when(clienteService.existeCliente(999L)).thenReturn(false);

        // When / Then
        assertThrows(BusinessRuleException.class, () -> citaService.save(request));
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("save: lanza BusinessRuleException cuando el técnico no existe")
    void save_deberiaLanzarExcepcion_cuandoTecnicoNoExiste() {
        // Given
        CitaRequest request = new CitaRequest(1L, 999L, fechaFutura, null);
        when(clienteService.existeCliente(1L)).thenReturn(true);
        when(tecnicoService.existeTecnico(999L)).thenReturn(false);

        // When / Then
        assertThrows(BusinessRuleException.class, () -> citaService.save(request));
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("save: lanza BusinessRuleException cuando la fecha de la cita es pasada")
    void save_deberiaLanzarExcepcion_cuandoFechaPasada() {
        // Given
        LocalDateTime fechaPasada = LocalDateTime.now().minusDays(1);
        CitaRequest request = new CitaRequest(1L, 1L, fechaPasada, null);
        lenient().when(clienteService.existeCliente(1L)).thenReturn(true);
        lenient().when(tecnicoService.existeTecnico(1L)).thenReturn(true);

        // When / Then
        assertThrows(BusinessRuleException.class, () -> citaService.save(request));
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("save: lanza DuplicateResourceException cuando el técnico ya tiene una cita a esa hora")
    void save_deberiaLanzarExcepcion_cuandoTecnicoYaTieneCitaAEsaHora() {
        // Given
        CitaRequest request = new CitaRequest(1L, 1L, fechaFutura, null);
        when(clienteService.existeCliente(1L)).thenReturn(true);
        when(tecnicoService.existeTecnico(1L)).thenReturn(true);
        when(citaRepository.existsByTecnicoIdAndFechaHoraAndActivoTrue(1L, fechaFutura)).thenReturn(true);

        // When / Then
        assertThrows(DuplicateResourceException.class, () -> citaService.save(request));
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("findById: lanza ResourceNotFoundException cuando no existe")
    void findById_deberiaLanzarExcepcion_cuandoNoExiste() {
        // Given
        when(citaRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> citaService.findById(99L));
    }

    @Test
    @DisplayName("update: lanza BusinessRuleException cuando la cita está cancelada")
    void update_deberiaLanzarExcepcion_cuandoCitaCancelada() {
        // Given
        cita.setActivo(false);
        CitaRequest request = new CitaRequest(1L, 1L, fechaFutura, null);
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));

        // When / Then
        assertThrows(BusinessRuleException.class, () -> citaService.update(1L, request));
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("cancelar: marca la cita como inactiva (eliminación lógica)")
    void cancelar_deberiaCancelarCita() {
        // Given
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        citaService.cancelar(1L);

        // Then
        assertFalse(cita.getActivo());
        verify(citaRepository).save(cita);
    }
}
