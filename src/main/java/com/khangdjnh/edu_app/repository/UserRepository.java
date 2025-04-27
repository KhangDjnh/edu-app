package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername (String username);

    @Override
    Optional<User> findById(Long id);
}
