package com.duocuc.technician.repository;

import com.duocuc.technician.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    boolean existsByNameIgnoreCase(String name);
}
