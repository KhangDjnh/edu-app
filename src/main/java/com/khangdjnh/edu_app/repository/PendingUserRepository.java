package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.PendingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingUserRepository extends JpaRepository<PendingUser, Long> {
    Optional<PendingUser> findByConfirmationToken(String token);
    boolean existsByEmail(String email);
}