package com.duocuc.part.service.impl;

import com.duocuc.part.client.SupplierClient;
import com.duocuc.part.service.SupplierService;
import org.springframework.stereotype.Service;

// Wraps SupplierClient: the rest of the service layer never depends on the HTTP client directly.
@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierClient supplierClient;

    public SupplierServiceImpl(SupplierClient supplierClient) {
        this.supplierClient = supplierClient;
    }

    @Override
    public boolean existsSupplier(Long supplierId) {
        return supplierClient.existsSupplier(supplierId);
    }
}
