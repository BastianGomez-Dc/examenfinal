package com.duocuc.equipment.controller;

import com.duocuc.equipment.dto.EquipmentTypeRequest;
import com.duocuc.equipment.dto.EquipmentTypeResponse;
import com.duocuc.equipment.service.EquipmentTypeService;
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
@RequestMapping("/equipment-types")
@Tag(name = "Equipment Types", description = "Catalog of equipment types (laptop, desktop, printer, etc.)")
public class EquipmentTypeController {

    private final EquipmentTypeService equipmentTypeService;

    public EquipmentTypeController(EquipmentTypeService equipmentTypeService) {
        this.equipmentTypeService = equipmentTypeService;
    }

    @Operation(summary = "Create an equipment type")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Equipment type created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "Name already registered")
    })
    @PostMapping
    public ResponseEntity<EquipmentTypeResponse> create(@RequestBody @Valid EquipmentTypeRequest request) {
        EquipmentTypeResponse response = equipmentTypeService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List all equipment types")
    @ApiResponse(responseCode = "200", description = "List of equipment types")
    @GetMapping
    public ResponseEntity<List<EquipmentTypeResponse>> findAll() {
        return ResponseEntity.ok(equipmentTypeService.findAll());
    }

    @Operation(summary = "Get an equipment type by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Equipment type found"),
            @ApiResponse(responseCode = "404", description = "Equipment type does not exist")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EquipmentTypeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentTypeService.findById(id));
    }

    @Operation(summary = "Update an equipment type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Equipment type updated"),
            @ApiResponse(responseCode = "404", description = "Equipment type does not exist"),
            @ApiResponse(responseCode = "409", description = "Name already registered"),
            @ApiResponse(responseCode = "422", description = "Equipment type is inactive")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EquipmentTypeResponse> update(@PathVariable Long id, @RequestBody @Valid EquipmentTypeRequest request) {
        return ResponseEntity.ok(equipmentTypeService.update(id, request));
    }

    @Operation(summary = "Deactivate an equipment type (logical delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Equipment type deactivated"),
            @ApiResponse(responseCode = "404", description = "Equipment type does not exist")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        equipmentTypeService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
