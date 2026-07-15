package com.duocuc.invoice.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InvoiceRepositoryTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Test
    @DisplayName("existsByOrderId: returns true when the order was already invoiced in the seed")
    void existsByOrderId_shouldReturnTrue_whenPresentInSeed() {
        assertTrue(invoiceRepository.existsByOrderId(4L));
    }

    @Test
    @DisplayName("existsByOrderId: returns false when the order has not been invoiced")
    void existsByOrderId_shouldReturnFalse_whenNotPresent() {
        assertFalse(invoiceRepository.existsByOrderId(999L));
    }

    @Test
    @DisplayName("findAll: returns the invoice loaded by the seed migration")
    void findAll_shouldReturnSeedInvoice() {
        assertEquals(1, invoiceRepository.findAll().size());
    }
}
