package com.duocuc.invoice.service;

import com.duocuc.invoice.client.dto.RemoteOrder;
import com.duocuc.invoice.dto.InvoiceRequest;
import com.duocuc.invoice.dto.InvoiceResponse;
import com.duocuc.invoice.entity.Invoice;
import com.duocuc.invoice.exception.BusinessRuleException;
import com.duocuc.invoice.exception.DuplicateResourceException;
import com.duocuc.invoice.exception.ResourceNotFoundException;
import com.duocuc.invoice.repository.InvoiceRepository;
import com.duocuc.invoice.service.impl.InvoiceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * OrderService (local wrapper around OrderClient) is mocked because it represents remote
 * communication with the order service.
 */
@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    private Invoice invoice;

    @BeforeEach
    void setUp() {
        invoice = new Invoice();
        invoice.setId(1L);
        invoice.setOrderId(3L);
        invoice.setTotal(new BigDecimal("25000.00"));
    }

    @Test
    @DisplayName("save: creates an invoice when the order exists and is in READY status")
    void save_shouldCreateInvoice_whenOrderReadyAndNotInvoiced() {
        // Given
        InvoiceRequest request = new InvoiceRequest(3L);
        when(orderService.findOrder(3L)).thenReturn(Optional.of(new RemoteOrder(3L, "READY", new BigDecimal("25000.00"))));
        when(invoiceRepository.existsByOrderId(3L)).thenReturn(false);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        // When
        InvoiceResponse response = invoiceService.save(request);

        // Then
        assertEquals(3L, response.orderId());
        assertEquals(new BigDecimal("25000.00"), response.total());
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    @DisplayName("save: creates an invoice when the order is in DELIVERED status")
    void save_shouldCreateInvoice_whenOrderDelivered() {
        // Given
        InvoiceRequest request = new InvoiceRequest(4L);
        when(orderService.findOrder(4L)).thenReturn(Optional.of(new RemoteOrder(4L, "DELIVERED", new BigDecimal("18000.00"))));
        when(invoiceRepository.existsByOrderId(4L)).thenReturn(false);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        // When / Then
        assertEquals(3L, invoiceService.save(request).orderId());
    }

    @Test
    @DisplayName("save: throws BusinessRuleException when the order does not exist")
    void save_shouldThrowException_whenOrderDoesNotExist() {
        // Given
        InvoiceRequest request = new InvoiceRequest(999L);
        when(orderService.findOrder(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(BusinessRuleException.class, () -> invoiceService.save(request));
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    @DisplayName("save: throws DuplicateResourceException when the order has already been invoiced")
    void save_shouldThrowException_whenOrderAlreadyInvoiced() {
        // Given
        InvoiceRequest request = new InvoiceRequest(4L);
        when(orderService.findOrder(4L)).thenReturn(Optional.of(new RemoteOrder(4L, "DELIVERED", new BigDecimal("18000.00"))));
        when(invoiceRepository.existsByOrderId(4L)).thenReturn(true);

        // When / Then
        assertThrows(DuplicateResourceException.class, () -> invoiceService.save(request));
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    @DisplayName("save: throws BusinessRuleException when the order is not in an invoiceable status")
    void save_shouldThrowException_whenOrderNotInvoiceable() {
        // Given
        InvoiceRequest request = new InvoiceRequest(1L);
        when(orderService.findOrder(1L)).thenReturn(Optional.of(new RemoteOrder(1L, "RECEIVED", new BigDecimal("15000.00"))));
        when(invoiceRepository.existsByOrderId(1L)).thenReturn(false);

        // When / Then
        assertThrows(BusinessRuleException.class, () -> invoiceService.save(request));
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    @DisplayName("findById: throws ResourceNotFoundException when it does not exist")
    void findById_shouldThrowException_whenNotFound() {
        // Given
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> invoiceService.findById(99L));
    }
}
