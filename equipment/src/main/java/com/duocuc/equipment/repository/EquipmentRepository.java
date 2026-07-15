package com.duocuc.equipment.repository;

import com.duocuc.equipment.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    boolean existsBySerialNumber(String serialNumber);
}
