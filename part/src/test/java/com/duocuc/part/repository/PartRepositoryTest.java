package com.duocuc.part.repository;

import com.duocuc.part.entity.Part;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PartRepositoryTest {

    @Autowired
    private PartRepository partRepository;

    @Test
    @DisplayName("findAll: returns the 5 parts loaded by the seed migration")
    void findAll_shouldReturnSeedParts() {
        assertEquals(5, partRepository.findAll().size());
    }

    @Test
    @DisplayName("save + findById: persists and retrieves a new part")
    void save_shouldPersistAndRetrievePart() {
        Part part = new Part();
        part.setName("USB Keyboard");
        part.setDescription("Replacement keyboard");
        part.setPrice(new BigDecimal("12000.00"));
        part.setStock(10);
        part.setSupplierId(1L);

        Part saved = partRepository.save(part);

        Optional<Part> found = partRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("USB Keyboard", found.get().getName());
    }
}
