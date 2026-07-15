package com.duocuc.order.service.impl;

import com.duocuc.order.client.ServiceCatalogClient;
import com.duocuc.order.client.dto.MaintenanceServiceSummary;
import com.duocuc.order.service.ServiceCatalogService;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Wraps ServiceCatalogClient: the rest of the service layer never depends on the HTTP client directly.
@Service
public class ServiceCatalogServiceImpl implements ServiceCatalogService {

    private final ServiceCatalogClient serviceCatalogClient;

    public ServiceCatalogServiceImpl(ServiceCatalogClient serviceCatalogClient) {
        this.serviceCatalogClient = serviceCatalogClient;
    }

    @Override
    public Optional<MaintenanceServiceSummary> findService(Long serviceId) {
        return serviceCatalogClient.findService(serviceId);
    }
}
