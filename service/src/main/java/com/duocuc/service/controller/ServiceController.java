package com.duocuc.service.controller;

import com.duocuc.service.dto.MaintenanceServiceRequest;
import com.duocuc.service.dto.MaintenanceServiceResponse;
import com.duocuc.service.service.ServiceCatalogService;
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
@RequestMapping("/services")
@Tag(name = "Services", description = "Catalog of maintenance services offered by TecnoFix")
public class ServiceController {

    private final ServiceCatalogService serviceCatalogService;

    public ServiceController(ServiceCatalogService serviceCatalogService) {
        this.serviceCatalogService = serviceCatalogService;
    }

    @Operation(summary = "Create a service")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Service created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "Name already registered in the category"),
            @ApiResponse(responseCode = "422", description = "Invalid price or category does not exist")
    })
    @PostMapping
    public ResponseEntity<MaintenanceServiceResponse> create(@RequestBody @Valid MaintenanceServiceRequest request) {
        MaintenanceServiceResponse response = serviceCatalogService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List all services")
    @ApiResponse(responseCode = "200", description = "List of services")
    @GetMapping
    public ResponseEntity<List<MaintenanceServiceResponse>> findAll() {
        return ResponseEntity.ok(serviceCatalogService.findAll());
    }

    @Operation(summary = "Get a service by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service found"),
            @ApiResponse(responseCode = "404", description = "Service does not exist")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceServiceResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceCatalogService.findById(id));
    }

    @Operation(summary = "Update a service")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service updated"),
            @ApiResponse(responseCode = "404", description = "Service does not exist"),
            @ApiResponse(responseCode = "409", description = "Name already registered in the category"),
            @ApiResponse(responseCode = "422", description = "Service is inactive, invalid price or category does not exist")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceServiceResponse> update(@PathVariable Long id, @RequestBody @Valid MaintenanceServiceRequest request) {
        return ResponseEntity.ok(serviceCatalogService.update(id, request));
    }

    @Operation(summary = "Deactivate a service (logical delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Service deactivated"),
            @ApiResponse(responseCode = "404", description = "Service does not exist")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        serviceCatalogService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
