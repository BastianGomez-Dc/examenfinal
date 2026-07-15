package com.duocuc.service.service.impl;

import com.duocuc.service.dto.MaintenanceServiceRequest;
import com.duocuc.service.dto.MaintenanceServiceResponse;
import com.duocuc.service.entity.Category;
import com.duocuc.service.entity.MaintenanceService;
import com.duocuc.service.exception.BusinessRuleException;
import com.duocuc.service.exception.DuplicateResourceException;
import com.duocuc.service.exception.ResourceNotFoundException;
import com.duocuc.service.repository.CategoryRepository;
import com.duocuc.service.repository.MaintenanceServiceRepository;
import com.duocuc.service.service.ServiceCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ServiceCatalogServiceImpl implements ServiceCatalogService {

    private static final Logger log = LoggerFactory.getLogger(ServiceCatalogServiceImpl.class);

    private final MaintenanceServiceRepository maintenanceServiceRepository;
    private final CategoryRepository categoryRepository;

    public ServiceCatalogServiceImpl(MaintenanceServiceRepository maintenanceServiceRepository, CategoryRepository categoryRepository) {
        this.maintenanceServiceRepository = maintenanceServiceRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public MaintenanceServiceResponse save(MaintenanceServiceRequest request) {
        // Business rule: the price must be greater than 0
        if (request.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("The service price must be greater than 0");
        }
        // Business rule: the category must exist
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessRuleException("The specified category does not exist: " + request.categoryId()));

        // Business rule: the service name must be unique within its category
        if (maintenanceServiceRepository.existsByNameIgnoreCaseAndCategoryId(request.name(), request.categoryId())) {
            throw new DuplicateResourceException(
                    "A service named '" + request.name() + "' already exists in the specified category");
        }

        MaintenanceService service = new MaintenanceService();
        service.setName(request.name());
        service.setDescription(request.description());
        service.setPrice(request.price());
        service.setCategory(category);
        MaintenanceService saved = maintenanceServiceRepository.save(service);
        log.info("Service created with id={} name={}", saved.getId(), saved.getName());
        return toResponse(saved);
    }

    @Override
    public List<MaintenanceServiceResponse> findAll() {
        log.info("Listing all services");
        return maintenanceServiceRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public MaintenanceServiceResponse findById(Long id) {
        MaintenanceService service = maintenanceServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        return toResponse(service);
    }

    @Override
    public MaintenanceServiceResponse update(Long id, MaintenanceServiceRequest request) {
        MaintenanceService service = maintenanceServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));

        if (Boolean.FALSE.equals(service.getActive())) {
            throw new BusinessRuleException("Cannot modify an inactive service (id: " + id + ")");
        }
        if (request.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("The service price must be greater than 0");
        }
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessRuleException("The specified category does not exist: " + request.categoryId()));

        boolean nameOrCategoryChanged = !service.getName().equalsIgnoreCase(request.name())
                || !service.getCategory().getId().equals(request.categoryId());
        if (nameOrCategoryChanged
                && maintenanceServiceRepository.existsByNameIgnoreCaseAndCategoryId(request.name(), request.categoryId())) {
            throw new DuplicateResourceException(
                    "A service named '" + request.name() + "' already exists in the specified category");
        }

        service.setName(request.name());
        service.setDescription(request.description());
        service.setPrice(request.price());
        service.setCategory(category);
        MaintenanceService updated = maintenanceServiceRepository.save(service);
        log.info("Service updated id={}", updated.getId());
        return toResponse(updated);
    }

    @Override
    public void deactivate(Long id) {
        MaintenanceService service = maintenanceServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        service.setActive(false);
        maintenanceServiceRepository.save(service);
        log.info("Service deactivated id={}", id);
    }

    private MaintenanceServiceResponse toResponse(MaintenanceService s) {
        return new MaintenanceServiceResponse(
                s.getId(), s.getName(), s.getDescription(), s.getPrice(),
                s.getCategory().getId(), s.getCategory().getName(), s.getActive()
        );
    }
}
