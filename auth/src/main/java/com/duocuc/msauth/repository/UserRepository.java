package com.duocuc.msauth.repository;

import com.duocuc.msauth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmailAndActiveTrue(String email);
}
