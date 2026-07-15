package com.duocuc.service.repository;

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
class MaintenanceServiceRepositoryTest {

    @Autowired
    private MaintenanceServiceRepository maintenanceServiceRepository;

    @Test
    @DisplayName("existsByNameIgnoreCaseAndCategoryId: returns true when the service exists in that category")
    void existsByNameIgnoreCaseAndCategoryId_shouldReturnTrue_whenExistsInSeed() {
        assertTrue(maintenanceServiceRepository.existsByNameIgnoreCaseAndCategoryId("general diagnostics", 1L));
    }

    @Test
    @DisplayName("existsByNameIgnoreCaseAndCategoryId: returns false when the same name is in another category")
    void existsByNameIgnoreCaseAndCategoryId_shouldReturnFalse_whenOtherCategory() {
        assertFalse(maintenanceServiceRepository.existsByNameIgnoreCaseAndCategoryId("general diagnostics", 2L));
    }

    @Test
    @DisplayName("findAll: returns the 5 services loaded by the seed migration")
    void findAll_shouldReturnSeedServices() {
        assertEquals(5, maintenanceServiceRepository.findAll().size());
    }
}
