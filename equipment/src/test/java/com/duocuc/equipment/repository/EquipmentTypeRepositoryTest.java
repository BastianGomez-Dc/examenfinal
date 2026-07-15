package com.duocuc.equipment.repository;

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
class EquipmentTypeRepositoryTest {

    @Autowired
    private EquipmentTypeRepository equipmentTypeRepository;

    @Test
    @DisplayName("existsByNameIgnoreCase: returns true when the name exists in the seed data (case-insensitive)")
    void existsByNameIgnoreCase_shouldReturnTrue_whenExistsInSeed() {
        assertTrue(equipmentTypeRepository.existsByNameIgnoreCase("laptop"));
    }

    @Test
    @DisplayName("existsByNameIgnoreCase: returns false when the name does not exist")
    void existsByNameIgnoreCase_shouldReturnFalse_whenNotExists() {
        assertFalse(equipmentTypeRepository.existsByNameIgnoreCase("Nonexistent"));
    }

    @Test
    @DisplayName("findAll: returns the 5 equipment types loaded by the seed migration")
    void findAll_shouldReturnSeedTypes() {
        assertEquals(5, equipmentTypeRepository.findAll().size());
    }
}
