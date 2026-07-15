package com.duocuc.equipment.repository;

import com.duocuc.equipment.entity.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentTypeRepository extends JpaRepository<EquipmentType, Long> {

    boolean existsByNameIgnoreCase(String name);
}
