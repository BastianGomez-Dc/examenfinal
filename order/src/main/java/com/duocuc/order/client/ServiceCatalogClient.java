package com.duocuc.order.client;

import com.duocuc.order.client.dto.MaintenanceServiceSummary;

import java.util.Optional;

public interface ServiceCatalogClient {

    // Returns the service (with current name and price) or empty if it does not exist in the service catalog.
    Optional<MaintenanceServiceSummary> findService(Long serviceId);
}
