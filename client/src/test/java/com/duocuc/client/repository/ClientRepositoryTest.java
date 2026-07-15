package com.duocuc.client.repository;

import com.duocuc.client.entity.Client;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Repository test with H2 in memory. Flyway runs the migrations (V1 creates the
 * table, V2 seeds the 5 sample clients) when the context starts, so tests can
 * query real data right away.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    @DisplayName("existsByEmail: returns true when the email exists in the seed data")
    void existsByEmail_shouldReturnTrue_whenEmailExistsInSeed() {
        assertTrue(clientRepository.existsByEmail("ana.torres@mail.cl"));
    }

    @Test
    @DisplayName("existsByEmail: returns false when the email does not exist")
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
        assertFalse(clientRepository.existsByEmail("does-not-exist@mail.cl"));
    }

    @Test
    @DisplayName("findByEmail: returns the client when the email exists")
    void findByEmail_shouldReturnClient_whenExists() {
        Optional<Client> client = clientRepository.findByEmail("ana.torres@mail.cl");

        assertTrue(client.isPresent());
        assertEquals("Ana Torres", client.get().getName());
    }

    @Test
    @DisplayName("findByEmail: returns empty when the email does not exist")
    void findByEmail_shouldReturnEmpty_whenNotExists() {
        Optional<Client> client = clientRepository.findByEmail("does-not-exist@mail.cl");

        assertFalse(client.isPresent());
    }

    @Test
    @DisplayName("findAll: returns the 5 clients loaded by the seed migration")
    void findAll_shouldReturnSeedClients() {
        assertEquals(5, clientRepository.findAll().size());
    }
}
