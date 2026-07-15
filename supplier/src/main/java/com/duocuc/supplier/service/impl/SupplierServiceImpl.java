package com.duocuc.supplier.service.impl;

import com.duocuc.supplier.dto.SupplierRequest;
import com.duocuc.supplier.dto.SupplierResponse;
import com.duocuc.supplier.entity.Supplier;
import com.duocuc.supplier.exception.BusinessRuleException;
import com.duocuc.supplier.exception.DuplicateResourceException;
import com.duocuc.supplier.exception.ResourceNotFoundException;
import com.duocuc.supplier.repository.SupplierRepository;
import com.duocuc.supplier.service.SupplierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    private static final Logger log = LoggerFactory.getLogger(SupplierServiceImpl.class);

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public SupplierResponse save(SupplierRequest request) {
        // Business rule: the email must be unique across the system
        if (supplierRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("A supplier already exists with email: " + request.email());
        }
        Supplier supplier = new Supplier();
        supplier.setName(request.name());
        supplier.setEmail(request.email());
        supplier.setPhone(request.phone());
        supplier.setAddress(request.address());
        Supplier saved = supplierRepository.save(supplier);
        log.info("Supplier created with id={} email={}", saved.getId(), saved.getEmail());
        return toResponse(saved);
    }

    @Override
    public List<SupplierResponse> findAll() {
        log.info("Listing all suppliers");
        return supplierRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public SupplierResponse findById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        return toResponse(supplier);
    }

    @Override
    public SupplierResponse update(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        // Business rule: an inactive supplier cannot be modified
        if (Boolean.FALSE.equals(supplier.getActive())) {
            throw new BusinessRuleException("Cannot modify an inactive supplier (id: " + id + ")");
        }
        if (!supplier.getEmail().equals(request.email()) && supplierRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("A supplier already exists with email: " + request.email());
        }
        supplier.setName(request.name());
        supplier.setEmail(request.email());
        supplier.setPhone(request.phone());
        supplier.setAddress(request.address());
        Supplier updated = supplierRepository.save(supplier);
        log.info("Supplier updated id={}", updated.getId());
        return toResponse(updated);
    }

    @Override
    public void deactivate(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        // Business rule: deletion is logical (deactivated, never removed)
        supplier.setActive(false);
        supplierRepository.save(supplier);
        log.info("Supplier deactivated id={}", id);
    }

    private SupplierResponse toResponse(Supplier s) {
        return new SupplierResponse(
                s.getId(), s.getName(), s.getEmail(),
                s.getPhone(), s.getAddress(), s.getActive(), s.getRegistrationDate()
        );
    }
}
