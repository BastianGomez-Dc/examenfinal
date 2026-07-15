package com.duocuc.msauth.repository;

import com.duocuc.msauth.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba de repositorio con H2 en memoria. Se usa una migración de prueba (V1 sin
 * pgcrypto, que H2 no soporta) y los usuarios se insertan directamente vía el
 * repositorio en lugar de depender de la semilla SQL con crypt().
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(new User("activo@mail.cl", "hash-cualquiera"));
        User inactivo = new User("inactivo@mail.cl", "hash-cualquiera");
        inactivo.setActive(false);
        userRepository.save(inactivo);
    }

    @Test
    @DisplayName("findByEmailAndActiveTrue: retorna el usuario cuando está activo")
    void findByEmailAndActiveTrue_deberiaRetornarUsuario_cuandoActivo() {
        Optional<User> user = userRepository.findByEmailAndActiveTrue("activo@mail.cl");
        assertTrue(user.isPresent());
    }

    @Test
    @DisplayName("findByEmailAndActiveTrue: retorna vacío cuando el usuario está inactivo")
    void findByEmailAndActiveTrue_deberiaRetornarVacio_cuandoInactivo() {
        Optional<User> user = userRepository.findByEmailAndActiveTrue("inactivo@mail.cl");
        assertFalse(user.isPresent());
    }

    @Test
    @DisplayName("findByEmailAndActiveTrue: retorna vacío cuando el email no existe")
    void findByEmailAndActiveTrue_deberiaRetornarVacio_cuandoNoExiste() {
        Optional<User> user = userRepository.findByEmailAndActiveTrue("no-existe@mail.cl");
        assertFalse(user.isPresent());
    }
}
