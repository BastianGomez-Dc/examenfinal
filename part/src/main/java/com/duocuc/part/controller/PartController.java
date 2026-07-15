package com.duocuc.part.controller;

import com.duocuc.part.dto.PartRequest;
import com.duocuc.part.dto.PartResponse;
import com.duocuc.part.dto.StockAdjustmentRequest;
import com.duocuc.part.service.PartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/parts")
@Tag(name = "Parts", description = "Management of TecnoFix parts inventory")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @Operation(summary = "Create a part")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Part created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "422", description = "Negative stock or supplier does not exist"),
            @ApiResponse(responseCode = "502", description = "Supplier service unavailable")
    })
    @PostMapping
    public ResponseEntity<PartResponse> create(@RequestBody @Valid PartRequest request) {
        PartResponse response = partService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List all parts")
    @ApiResponse(responseCode = "200", description = "List of parts")
    @GetMapping
    public ResponseEntity<List<PartResponse>> findAll() {
        return ResponseEntity.ok(partService.findAll());
    }

    @Operation(summary = "Get a part by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Part found"),
            @ApiResponse(responseCode = "404", description = "Part does not exist")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PartResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(partService.findById(id));
    }

    @Operation(summary = "Update a part")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Part updated"),
            @ApiResponse(responseCode = "404", description = "Part does not exist"),
            @ApiResponse(responseCode = "422", description = "Part inactive, negative stock or supplier does not exist")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PartResponse> update(@PathVariable Long id, @RequestBody @Valid PartRequest request) {
        return ResponseEntity.ok(partService.update(id, request));
    }

    @Operation(summary = "Subtract stock from a part")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock subtracted"),
            @ApiResponse(responseCode = "404", description = "Part does not exist"),
            @ApiResponse(responseCode = "422", description = "Insufficient stock")
    })
    @PatchMapping("/{id}/stock")
    public ResponseEntity<PartResponse> subtractStock(@PathVariable Long id, @RequestBody @Valid StockAdjustmentRequest request) {
        return ResponseEntity.ok(partService.subtractStock(id, request));
    }

    @Operation(summary = "Deactivate a part (logical delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Part deactivated"),
            @ApiResponse(responseCode = "404", description = "Part does not exist")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        partService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
