package com.duocuc.invoice.service.impl;

import com.duocuc.invoice.client.dto.RemoteOrder;
import com.duocuc.invoice.dto.InvoiceRequest;
import com.duocuc.invoice.dto.InvoiceResponse;
import com.duocuc.invoice.entity.Invoice;
import com.duocuc.invoice.exception.BusinessRuleException;
import com.duocuc.invoice.exception.DuplicateResourceException;
import com.duocuc.invoice.exception.ResourceNotFoundException;
import com.duocuc.invoice.repository.InvoiceRepository;
import com.duocuc.invoice.service.InvoiceService;
import com.duocuc.invoice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);

    private static final Set<String> INVOICEABLE_STATUSES = Set.of("READY", "DELIVERED");

    private final InvoiceRepository invoiceRepository;
    private final OrderService orderService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, OrderService orderService) {
        this.invoiceRepository = invoiceRepository;
        this.orderService = orderService;
    }

    @Override
    public InvoiceResponse save(InvoiceRequest request) {
        // Business rule: the order must exist in the order service
        RemoteOrder order = orderService.findOrder(request.orderId())
                .orElseThrow(() -> new BusinessRuleException("The specified order does not exist: " + request.orderId()));

        // Business rule: an order cannot be invoiced twice
        if (invoiceRepository.existsByOrderId(request.orderId())) {
            throw new DuplicateResourceException("The order has already been invoiced: " + request.orderId());
        }
        // Business rule: only orders in READY or DELIVERED status can be invoiced
        if (!INVOICEABLE_STATUSES.contains(order.status())) {
            throw new BusinessRuleException(
                    "Only orders in READY or DELIVERED status can be invoiced (current status: " + order.status() + ")");
        }

        Invoice invoice = new Invoice();
        invoice.setOrderId(request.orderId());
        invoice.setTotal(order.total());
        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice created with id={} orderId={} total={}", saved.getId(), saved.getOrderId(), saved.getTotal());
        return toResponse(saved);
    }

    @Override
    public List<InvoiceResponse> findAll() {
        log.info("Listing all invoices");
        return invoiceRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public InvoiceResponse findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        return toResponse(invoice);
    }

    private InvoiceResponse toResponse(Invoice f) {
        return new InvoiceResponse(f.getId(), f.getOrderId(), f.getTotal(), f.getIssueDate());
    }
}
