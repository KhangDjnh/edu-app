package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.User;
import io.micrometer.common.lang.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername (String username);
    boolean existsById(@NonNull Long id);

    Optional<User> findByEmail(String email);
    Optional<User> findByKeycloakUserId(String keycloakUserId);
}
