package com.duocuc.service.service;

import com.duocuc.service.dto.MaintenanceServiceRequest;
import com.duocuc.service.dto.MaintenanceServiceResponse;

import java.util.List;

// Named ServiceCatalogService (not "ServiceService") to avoid the awkward CSR
// suffix collision with the MaintenanceService entity name.
public interface ServiceCatalogService {

    MaintenanceServiceResponse save(MaintenanceServiceRequest request);

    List<MaintenanceServiceResponse> findAll();

    MaintenanceServiceResponse findById(Long id);

    MaintenanceServiceResponse update(Long id, MaintenanceServiceRequest request);

    void deactivate(Long id);
}
