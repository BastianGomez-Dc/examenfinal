package com.duocuc.equipment.controller;

import com.duocuc.equipment.dto.EquipmentRequest;
import com.duocuc.equipment.dto.EquipmentResponse;
import com.duocuc.equipment.service.EquipmentService;
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
@RequestMapping("/equipment")
@Tag(name = "Equipment", description = "Management of equipment checked in for repair at TecnoFix")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @Operation(summary = "Create an equipment item")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Equipment created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "Serial number already registered"),
            @ApiResponse(responseCode = "422", description = "Client or equipment type does not exist"),
            @ApiResponse(responseCode = "502", description = "Client service unavailable")
    })
    @PostMapping
    public ResponseEntity<EquipmentResponse> create(@RequestBody @Valid EquipmentRequest request) {
        EquipmentResponse response = equipmentService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List all equipment")
    @ApiResponse(responseCode = "200", description = "List of equipment")
    @GetMapping
    public ResponseEntity<List<EquipmentResponse>> findAll() {
        return ResponseEntity.ok(equipmentService.findAll());
    }

    @Operation(summary = "Get an equipment item by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Equipment found"),
            @ApiResponse(responseCode = "404", description = "Equipment does not exist")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EquipmentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.findById(id));
    }

    @Operation(summary = "Update an equipment item")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Equipment updated"),
            @ApiResponse(responseCode = "404", description = "Equipment does not exist"),
            @ApiResponse(responseCode = "409", description = "Serial number already registered"),
            @ApiResponse(responseCode = "422", description = "Equipment inactive, client or equipment type does not exist")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EquipmentResponse> update(@PathVariable Long id, @RequestBody @Valid EquipmentRequest request) {
        return ResponseEntity.ok(equipmentService.update(id, request));
    }

    @Operation(summary = "Deactivate an equipment item (logical delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Equipment deactivated"),
            @ApiResponse(responseCode = "404", description = "Equipment does not exist")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        equipmentService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
