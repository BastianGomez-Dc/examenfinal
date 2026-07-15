package com.duocuc.invoice.controller;

import com.duocuc.invoice.dto.InvoiceRequest;
import com.duocuc.invoice.dto.InvoiceResponse;
import com.duocuc.invoice.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceControllerTest {

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

    private InvoiceResponse invoiceResponse;

    @BeforeEach
    void setUp() {
        invoiceResponse = new InvoiceResponse(1L, 3L, new BigDecimal("25000.00"), LocalDate.now());
    }

    @Test
    @DisplayName("create: returns 201 with the created invoice")
    void create_shouldReturn201() {
        // Given
        InvoiceRequest request = new InvoiceRequest(3L);
        when(invoiceService.save(any(InvoiceRequest.class))).thenReturn(invoiceResponse);

        // When
        ResponseEntity<InvoiceResponse> result = invoiceController.create(request);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(3L, result.getBody().orderId());
    }

    @Test
    @DisplayName("findAll: returns 200 with the list of invoices")
    void findAll_shouldReturn200() {
        // Given
        when(invoiceService.findAll()).thenReturn(List.of(invoiceResponse));

        // When
        ResponseEntity<List<InvoiceResponse>> result = invoiceController.findAll();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    @DisplayName("findById: returns 200 with the invoice found")
    void findById_shouldReturn200() {
        // Given
        when(invoiceService.findById(1L)).thenReturn(invoiceResponse);

        // When
        ResponseEntity<InvoiceResponse> result = invoiceController.findById(1L);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().id());
    }
}
