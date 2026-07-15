package com.duocuc.technician.repository;

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
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    @DisplayName("existsByNameIgnoreCase: returns true when the name exists in the seed data")
    void existsByNameIgnoreCase_shouldReturnTrue_whenExistsInSeed() {
        assertTrue(specialtyRepository.existsByNameIgnoreCase("hardware"));
    }

    @Test
    @DisplayName("existsByNameIgnoreCase: returns false when the name does not exist")
    void existsByNameIgnoreCase_shouldReturnFalse_whenNotExists() {
        assertFalse(specialtyRepository.existsByNameIgnoreCase("Nonexistent"));
    }

    @Test
    @DisplayName("findAll: returns the 5 specialties loaded by the seed migration")
    void findAll_shouldReturnSeedSpecialties() {
        assertEquals(5, specialtyRepository.findAll().size());
    }
}
