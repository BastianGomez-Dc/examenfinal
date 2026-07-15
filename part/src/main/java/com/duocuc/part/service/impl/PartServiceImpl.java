package com.duocuc.part.service.impl;

import com.duocuc.part.dto.PartRequest;
import com.duocuc.part.dto.PartResponse;
import com.duocuc.part.dto.StockAdjustmentRequest;
import com.duocuc.part.entity.Part;
import com.duocuc.part.exception.BusinessRuleException;
import com.duocuc.part.exception.ResourceNotFoundException;
import com.duocuc.part.repository.PartRepository;
import com.duocuc.part.service.PartService;
import com.duocuc.part.service.SupplierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartServiceImpl implements PartService {

    private static final Logger log = LoggerFactory.getLogger(PartServiceImpl.class);

    private final PartRepository partRepository;
    private final SupplierService supplierService;

    public PartServiceImpl(PartRepository partRepository, SupplierService supplierService) {
        this.partRepository = partRepository;
        this.supplierService = supplierService;
    }

    @Override
    public PartResponse save(PartRequest request) {
        // Business rule: stock cannot be negative
        if (request.stock() < 0) {
            throw new BusinessRuleException("Stock cannot be negative");
        }
        // Business rule: the supplier must exist in the supplier service
        if (!supplierService.existsSupplier(request.supplierId())) {
            throw new BusinessRuleException("The specified supplier does not exist: " + request.supplierId());
        }

        Part part = new Part();
        part.setName(request.name());
        part.setDescription(request.description());
        part.setPrice(request.price());
        part.setStock(request.stock());
        part.setSupplierId(request.supplierId());
        Part saved = partRepository.save(part);
        log.info("Part created with id={} name={}", saved.getId(), saved.getName());
        return toResponse(saved);
    }

    @Override
    public List<PartResponse> findAll() {
        log.info("Listing all parts");
        return partRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PartResponse findById(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part not found with id: " + id));
        return toResponse(part);
    }

    @Override
    public PartResponse update(Long id, PartRequest request) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part not found with id: " + id));

        if (Boolean.FALSE.equals(part.getActive())) {
            throw new BusinessRuleException("Cannot modify an inactive part (id: " + id + ")");
        }
        if (request.stock() < 0) {
            throw new BusinessRuleException("Stock cannot be negative");
        }
        if (!part.getSupplierId().equals(request.supplierId()) && !supplierService.existsSupplier(request.supplierId())) {
            throw new BusinessRuleException("The specified supplier does not exist: " + request.supplierId());
        }

        part.setName(request.name());
        part.setDescription(request.description());
        part.setPrice(request.price());
        part.setStock(request.stock());
        part.setSupplierId(request.supplierId());
        Part updated = partRepository.save(part);
        log.info("Part updated id={}", updated.getId());
        return toResponse(updated);
    }

    @Override
    public PartResponse subtractStock(Long id, StockAdjustmentRequest request) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part not found with id: " + id));

        // Business rule: cannot subtract more stock than what is available
        if (request.quantity() > part.getStock()) {
            throw new BusinessRuleException(
                    "Insufficient stock: available " + part.getStock() + ", requested " + request.quantity());
        }
        part.setStock(part.getStock() - request.quantity());
        Part updated = partRepository.save(part);
        log.info("Stock subtracted id={} quantity={} remainingStock={}", id, request.quantity(), updated.getStock());
        return toResponse(updated);
    }

    @Override
    public void deactivate(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part not found with id: " + id));
        part.setActive(false);
        partRepository.save(part);
        log.info("Part deactivated id={}", id);
    }

    private PartResponse toResponse(Part p) {
        return new PartResponse(
                p.getId(), p.getName(), p.getDescription(), p.getPrice(),
                p.getStock(), p.getSupplierId(), p.getActive()
        );
    }
}
