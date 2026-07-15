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
class TechnicianRepositoryTest {

    @Autowired
    private TechnicianRepository technicianRepository;

    @Test
    @DisplayName("existsByEmail: returns true when the email exists in the seed data")
    void existsByEmail_shouldReturnTrue_whenExistsInSeed() {
        assertTrue(technicianRepository.existsByEmail("javier.soto@tecnofix.cl"));
    }

    @Test
    @DisplayName("existsByEmail: returns false when the email does not exist")
    void existsByEmail_shouldReturnFalse_whenNotExists() {
        assertFalse(technicianRepository.existsByEmail("does-not-exist@tecnofix.cl"));
    }

    @Test
    @DisplayName("findAll: returns the 5 technicians loaded by the seed migration")
    void findAll_shouldReturnSeedTechnicians() {
        assertEquals(5, technicianRepository.findAll().size());
    }
}
