package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername (String username);
    boolean existsById(Long id);

    @Override
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByKeycloakUserId(String keycloakUserId);
}
