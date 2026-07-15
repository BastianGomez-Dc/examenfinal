package com.duocuc.supplier.service;

import com.duocuc.supplier.dto.SupplierRequest;
import com.duocuc.supplier.dto.SupplierResponse;

import java.util.List;

public interface SupplierService {

    SupplierResponse save(SupplierRequest request);

    List<SupplierResponse> findAll();

    SupplierResponse findById(Long id);

    SupplierResponse update(Long id, SupplierRequest request);

    void deactivate(Long id);
}
