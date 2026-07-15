package com.duocuc.client.controller;

import com.duocuc.client.dto.ClientRequest;
import com.duocuc.client.dto.ClientResponse;
import com.duocuc.client.service.ClientService;
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
@RequestMapping("/clients")
@Tag(name = "Clients", description = "TecnoFix client management")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Operation(summary = "Create a client")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    @PostMapping
    public ResponseEntity<ClientResponse> create(@RequestBody @Valid ClientRequest request) {
        ClientResponse response = clientService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List all clients")
    @ApiResponse(responseCode = "200", description = "List of clients")
    @GetMapping
    public ResponseEntity<List<ClientResponse>> findAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @Operation(summary = "Get a client by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client found"),
            @ApiResponse(responseCode = "404", description = "Client does not exist")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @Operation(summary = "Update a client")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client updated"),
            @ApiResponse(responseCode = "404", description = "Client does not exist"),
            @ApiResponse(responseCode = "409", description = "Email already registered"),
            @ApiResponse(responseCode = "422", description = "Client is inactive")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> update(@PathVariable Long id, @RequestBody @Valid ClientRequest request) {
        return ResponseEntity.ok(clientService.update(id, request));
    }

    @Operation(summary = "Deactivate a client (logical delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Client deactivated"),
            @ApiResponse(responseCode = "404", description = "Client does not exist")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        clientService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
