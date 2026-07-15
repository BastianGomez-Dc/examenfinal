package com.duocuc.msagenda.controller;

import com.duocuc.msagenda.dto.CitaRequest;
import com.duocuc.msagenda.dto.CitaResponse;
import com.duocuc.msagenda.service.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/citas")
@Tag(name = "Citas", description = "Agendamiento de citas entre clientes y técnicos de TecnoFix")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @Operation(summary = "Crear una cita")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cita creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "El técnico ya tiene una cita a esa hora"),
            @ApiResponse(responseCode = "422", description = "Cliente o técnico no existe, o fecha pasada"),
            @ApiResponse(responseCode = "502", description = "Un microservicio remoto no está disponible")
    })
    @PostMapping
    public ResponseEntity<CitaResponse> create(@RequestBody @Valid CitaRequest request) {
        CitaResponse response = citaService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar todas las citas")
    @ApiResponse(responseCode = "200", description = "Listado de citas")
    @GetMapping
    public ResponseEntity<List<CitaResponse>> findAll() {
        return ResponseEntity.ok(citaService.findAll());
    }

    @Operation(summary = "Obtener una cita por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cita encontrada"),
            @ApiResponse(responseCode = "404", description = "Cita no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CitaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.findById(id));
    }

    @Operation(summary = "Reagendar una cita")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cita actualizada"),
            @ApiResponse(responseCode = "404", description = "Cita no existe"),
            @ApiResponse(responseCode = "409", description = "El técnico ya tiene una cita a esa hora"),
            @ApiResponse(responseCode = "422", description = "Cita cancelada, cliente/técnico no existe o fecha pasada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CitaResponse> update(@PathVariable Long id, @RequestBody @Valid CitaRequest request) {
        return ResponseEntity.ok(citaService.update(id, request));
    }

    @Operation(summary = "Cancelar una cita (eliminación lógica)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cita cancelada"),
            @ApiResponse(responseCode = "404", description = "Cita no existe")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        citaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
