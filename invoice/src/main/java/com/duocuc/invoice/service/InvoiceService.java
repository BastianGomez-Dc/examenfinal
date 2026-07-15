package com.duocuc.invoice.service;

import com.duocuc.invoice.dto.InvoiceRequest;
import com.duocuc.invoice.dto.InvoiceResponse;

import java.util.List;

public interface InvoiceService {

    InvoiceResponse save(InvoiceRequest request);

    List<InvoiceResponse> findAll();

    InvoiceResponse findById(Long id);
}
