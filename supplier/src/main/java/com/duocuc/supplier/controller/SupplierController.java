package com.duocuc.supplier.controller;

import com.duocuc.supplier.dto.SupplierRequest;
import com.duocuc.supplier.dto.SupplierResponse;
import com.duocuc.supplier.service.SupplierService;
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
@RequestMapping("/suppliers")
@Tag(name = "Suppliers", description = "Management of TecnoFix parts suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @Operation(summary = "Create a supplier")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Supplier created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    @PostMapping
    public ResponseEntity<SupplierResponse> create(@RequestBody @Valid SupplierRequest request) {
        SupplierResponse response = supplierService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List all suppliers")
    @ApiResponse(responseCode = "200", description = "List of suppliers")
    @GetMapping
    public ResponseEntity<List<SupplierResponse>> findAll() {
        return ResponseEntity.ok(supplierService.findAll());
    }

    @Operation(summary = "Get a supplier by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplier found"),
            @ApiResponse(responseCode = "404", description = "Supplier does not exist")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.findById(id));
    }

    @Operation(summary = "Update a supplier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplier updated"),
            @ApiResponse(responseCode = "404", description = "Supplier does not exist"),
            @ApiResponse(responseCode = "409", description = "Email already registered"),
            @ApiResponse(responseCode = "422", description = "Supplier is inactive")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponse> update(@PathVariable Long id, @RequestBody @Valid SupplierRequest request) {
        return ResponseEntity.ok(supplierService.update(id, request));
    }

    @Operation(summary = "Deactivate a supplier (logical delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Supplier deactivated"),
            @ApiResponse(responseCode = "404", description = "Supplier does not exist")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        supplierService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
