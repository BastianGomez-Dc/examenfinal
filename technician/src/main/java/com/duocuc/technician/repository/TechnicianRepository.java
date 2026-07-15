package com.duocuc.technician.repository;

import com.duocuc.technician.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnicianRepository extends JpaRepository<Technician, Long> {

    boolean existsByEmail(String email);
}
