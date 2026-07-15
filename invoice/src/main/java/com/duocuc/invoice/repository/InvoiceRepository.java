package com.duocuc.invoice.repository;

import com.duocuc.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    boolean existsByOrderId(Long orderId);
}
