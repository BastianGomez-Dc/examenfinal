package com.duocuc.supplier.repository;

import com.duocuc.supplier.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    boolean existsByEmail(String email);
}
