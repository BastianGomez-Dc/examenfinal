package com.duocuc.order.service;

import com.duocuc.order.client.dto.MaintenanceServiceSummary;

import java.util.Optional;

public interface ServiceCatalogService {

    Optional<MaintenanceServiceSummary> findService(Long serviceId);
}
