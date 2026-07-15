package com.duocuc.service.repository;

import com.duocuc.service.entity.MaintenanceService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceServiceRepository extends JpaRepository<MaintenanceService, Long> {

    // Business rule: the service name must be unique within its category
    boolean existsByNameIgnoreCaseAndCategoryId(String name, Long categoryId);
}
