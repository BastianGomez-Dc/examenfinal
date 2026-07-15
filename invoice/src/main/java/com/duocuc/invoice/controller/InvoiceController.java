package com.duocuc.invoice.controller;

import com.duocuc.invoice.dto.InvoiceRequest;
import com.duocuc.invoice.dto.InvoiceResponse;
import com.duocuc.invoice.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/invoices")
@Tag(name = "Invoices", description = "TecnoFix work order invoicing")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Operation(summary = "Invoice an order")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Invoice created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "The order has already been invoiced"),
            @ApiResponse(responseCode = "422", description = "The order does not exist or is not in an invoiceable status"),
            @ApiResponse(responseCode = "502", description = "Order service unavailable")
    })
    @PostMapping
    public ResponseEntity<InvoiceResponse> create(@RequestBody @Valid InvoiceRequest request) {
        InvoiceResponse response = invoiceService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List all invoices")
    @ApiResponse(responseCode = "200", description = "List of invoices")
    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> findAll() {
        return ResponseEntity.ok(invoiceService.findAll());
    }

    @Operation(summary = "Get an invoice by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invoice found"),
            @ApiResponse(responseCode = "404", description = "Invoice does not exist")
    })
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.findById(id));
    }
}
