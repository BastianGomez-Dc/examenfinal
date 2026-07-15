package com.duocuc.msagenda.service.impl;

import com.duocuc.msagenda.dto.CitaRequest;
import com.duocuc.msagenda.dto.CitaResponse;
import com.duocuc.msagenda.entity.Cita;
import com.duocuc.msagenda.exception.BusinessRuleException;
import com.duocuc.msagenda.exception.DuplicateResourceException;
import com.duocuc.msagenda.exception.ResourceNotFoundException;
import com.duocuc.msagenda.repository.CitaRepository;
import com.duocuc.msagenda.service.CitaService;
import com.duocuc.msagenda.service.ClienteService;
import com.duocuc.msagenda.service.TecnicoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CitaServiceImpl implements CitaService {

    private static final Logger log = LoggerFactory.getLogger(CitaServiceImpl.class);

    private final CitaRepository citaRepository;
    private final ClienteService clienteService;
    private final TecnicoService tecnicoService;

    public CitaServiceImpl(CitaRepository citaRepository, ClienteService clienteService, TecnicoService tecnicoService) {
        this.citaRepository = citaRepository;
        this.clienteService = clienteService;
        this.tecnicoService = tecnicoService;
    }

    @Override
    public CitaResponse save(CitaRequest request) {
        // Regla de negocio: cliente y tecnico deben existir en sus respectivos microservicios
        if (!clienteService.existeCliente(request.clienteId())) {
            throw new BusinessRuleException("El cliente indicado no existe: " + request.clienteId());
        }
        if (!tecnicoService.existeTecnico(request.tecnicoId())) {
            throw new BusinessRuleException("El técnico indicado no existe: " + request.tecnicoId());
        }
        // Regla de negocio: la fecha de la cita no puede ser pasada
        if (request.fechaHora().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("La fecha y hora de la cita no puede ser pasada");
        }
        // Regla de negocio: un tecnico no puede tener dos citas vigentes a la misma hora
        if (citaRepository.existsByTecnicoIdAndFechaHoraAndActivoTrue(request.tecnicoId(), request.fechaHora())) {
            throw new DuplicateResourceException(
                    "El técnico ya tiene una cita agendada a esa hora: " + request.fechaHora());
        }

        Cita cita = new Cita();
        cita.setClienteId(request.clienteId());
        cita.setTecnicoId(request.tecnicoId());
        cita.setFechaHora(request.fechaHora());
        cita.setMotivo(request.motivo());
        Cita guardada = citaRepository.save(cita);
        log.info("Cita creada con id={} tecnicoId={} fechaHora={}", guardada.getId(), guardada.getTecnicoId(), guardada.getFechaHora());
        return toResponse(guardada);
    }

    @Override
    public List<CitaResponse> findAll() {
        log.info("Listando todas las citas");
        return citaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CitaResponse findById(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));
        return toResponse(cita);
    }

    @Override
    public CitaResponse update(Long id, CitaRequest request) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));

        if (Boolean.FALSE.equals(cita.getActivo())) {
            throw new BusinessRuleException("No se puede modificar una cita cancelada (id: " + id + ")");
        }
        if (!clienteService.existeCliente(request.clienteId())) {
            throw new BusinessRuleException("El cliente indicado no existe: " + request.clienteId());
        }
        if (!tecnicoService.existeTecnico(request.tecnicoId())) {
            throw new BusinessRuleException("El técnico indicado no existe: " + request.tecnicoId());
        }
        if (request.fechaHora().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("La fecha y hora de la cita no puede ser pasada");
        }
        if (citaRepository.existsByTecnicoIdAndFechaHoraAndActivoTrueAndIdNot(request.tecnicoId(), request.fechaHora(), id)) {
            throw new DuplicateResourceException(
                    "El técnico ya tiene una cita agendada a esa hora: " + request.fechaHora());
        }

        cita.setClienteId(request.clienteId());
        cita.setTecnicoId(request.tecnicoId());
        cita.setFechaHora(request.fechaHora());
        cita.setMotivo(request.motivo());
        Cita actualizada = citaRepository.save(cita);
        log.info("Cita actualizada id={}", actualizada.getId());
        return toResponse(actualizada);
    }

    @Override
    public void cancelar(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));
        cita.setActivo(false);
        citaRepository.save(cita);
        log.info("Cita cancelada id={}", id);
    }

    private CitaResponse toResponse(Cita c) {
        return new CitaResponse(c.getId(), c.getClienteId(), c.getTecnicoId(), c.getFechaHora(), c.getMotivo(), c.getActivo());
    }
}
