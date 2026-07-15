package com.duocuc.technician.controller;

import com.duocuc.technician.dto.TechnicianRequest;
import com.duocuc.technician.dto.TechnicianResponse;
import com.duocuc.technician.service.TechnicianService;
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
@RequestMapping("/technicians")
@Tag(name = "Technicians", description = "Management of TecnoFix technicians")
public class TechnicianController {

    private final TechnicianService technicianService;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    @Operation(summary = "Create a technician")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Technician created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "Email already registered"),
            @ApiResponse(responseCode = "422", description = "Specialty does not exist")
    })
    @PostMapping
    public ResponseEntity<TechnicianResponse> create(@RequestBody @Valid TechnicianRequest request) {
        TechnicianResponse response = technicianService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List all technicians")
    @ApiResponse(responseCode = "200", description = "List of technicians")
    @GetMapping
    public ResponseEntity<List<TechnicianResponse>> findAll() {
        return ResponseEntity.ok(technicianService.findAll());
    }

    @Operation(summary = "Get a technician by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Technician found"),
            @ApiResponse(responseCode = "404", description = "Technician does not exist")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TechnicianResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(technicianService.findById(id));
    }

    @Operation(summary = "Update a technician")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Technician updated"),
            @ApiResponse(responseCode = "404", description = "Technician does not exist"),
            @ApiResponse(responseCode = "409", description = "Email already registered"),
            @ApiResponse(responseCode = "422", description = "Technician is inactive or specialty does not exist")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TechnicianResponse> update(@PathVariable Long id, @RequestBody @Valid TechnicianRequest request) {
        return ResponseEntity.ok(technicianService.update(id, request));
    }

    @Operation(summary = "Deactivate a technician (logical delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Technician deactivated"),
            @ApiResponse(responseCode = "404", description = "Technician does not exist")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        technicianService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
