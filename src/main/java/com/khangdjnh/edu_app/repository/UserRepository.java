package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.enums.UserRole;
import io.micrometer.common.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername (String username);
    boolean existsById(@NonNull Long id);

    @Query("""
        SELECT u
        FROM User u
        WHERE
            u.isActive = true
            AND (
                :role IS NULL OR u.role = :role
            )
            AND (
                :commonSearch IS NULL OR
                LOWER(u.username) LIKE LOWER(CONCAT('%', :commonSearch, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :commonSearch, '%'))
                OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :commonSearch, '%'))
                OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :commonSearch, '%'))
                OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :commonSearch, '%'))
                OR LOWER(u.address) LIKE LOWER(CONCAT('%', :commonSearch, '%'))
                OR LOWER(CAST(u.gender AS string)) LIKE LOWER(CONCAT('%', :commonSearch, '%'))
                OR LOWER(CAST(u.role AS string)) LIKE LOWER(CONCAT('%', :commonSearch, '%'))
                OR LOWER(CAST(u.primarySubject AS string)) LIKE LOWER(CONCAT('%', :commonSearch, '%'))
            )
    """)
    Page<User> searchUsers(
            @Param("commonSearch") String commonSearch,
            @Param("role") UserRole role,
            Pageable pageable
    );

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByKeycloakUserId(String keycloakUserId);
}
