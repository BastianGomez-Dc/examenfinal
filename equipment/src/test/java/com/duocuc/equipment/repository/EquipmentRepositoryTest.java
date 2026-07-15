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
class EquipmentRepositoryTest {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    @DisplayName("existsBySerialNumber: returns true when the serial number exists in the seed data")
    void existsBySerialNumber_shouldReturnTrue_whenExistsInSeed() {
        assertTrue(equipmentRepository.existsBySerialNumber("SN-0001-NB"));
    }

    @Test
    @DisplayName("existsBySerialNumber: returns false when the serial number does not exist")
    void existsBySerialNumber_shouldReturnFalse_whenNotExists() {
        assertFalse(equipmentRepository.existsBySerialNumber("SN-9999-XX"));
    }

    @Test
    @DisplayName("findAll: returns the 5 equipment items loaded by the seed migration")
    void findAll_shouldReturnSeedEquipment() {
        assertEquals(5, equipmentRepository.findAll().size());
    }
}
