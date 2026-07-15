package com.duocuc.supplier.repository;

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
class SupplierRepositoryTest {

    @Autowired
    private SupplierRepository supplierRepository;

    @Test
    @DisplayName("existsByEmail: returns true when the email exists in the seed data")
    void existsByEmail_shouldReturnTrue_whenExistsInSeed() {
        assertTrue(supplierRepository.existsByEmail("contact@tecnoparts.cl"));
    }

    @Test
    @DisplayName("existsByEmail: returns false when the email does not exist")
    void existsByEmail_shouldReturnFalse_whenNotExists() {
        assertFalse(supplierRepository.existsByEmail("does-not-exist@mail.cl"));
    }

    @Test
    @DisplayName("findAll: returns the 5 suppliers loaded by the seed migration")
    void findAll_shouldReturnSeedSuppliers() {
        assertEquals(5, supplierRepository.findAll().size());
    }
}
