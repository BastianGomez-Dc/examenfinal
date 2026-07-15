package com.duocuc.technician.controller;

import com.duocuc.technician.dto.SpecialtyRequest;
import com.duocuc.technician.dto.SpecialtyResponse;
import com.duocuc.technician.service.SpecialtyService;
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
@RequestMapping("/specialties")
@Tag(name = "Specialties", description = "Catalog of technical specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @Operation(summary = "Create a specialty")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Specialty created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "Name already registered")
    })
    @PostMapping
    public ResponseEntity<SpecialtyResponse> create(@RequestBody @Valid SpecialtyRequest request) {
        SpecialtyResponse response = specialtyService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List all specialties")
    @ApiResponse(responseCode = "200", description = "List of specialties")
    @GetMapping
    public ResponseEntity<List<SpecialtyResponse>> findAll() {
        return ResponseEntity.ok(specialtyService.findAll());
    }

    @Operation(summary = "Get a specialty by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Specialty found"),
            @ApiResponse(responseCode = "404", description = "Specialty does not exist")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SpecialtyResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(specialtyService.findById(id));
    }

    @Operation(summary = "Update a specialty")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Specialty updated"),
            @ApiResponse(responseCode = "404", description = "Specialty does not exist"),
            @ApiResponse(responseCode = "409", description = "Name already registered"),
            @ApiResponse(responseCode = "422", description = "Specialty is inactive")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SpecialtyResponse> update(@PathVariable Long id, @RequestBody @Valid SpecialtyRequest request) {
        return ResponseEntity.ok(specialtyService.update(id, request));
    }

    @Operation(summary = "Deactivate a specialty (logical delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Specialty deactivated"),
            @ApiResponse(responseCode = "404", description = "Specialty does not exist")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        specialtyService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
