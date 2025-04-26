package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
