package com.khangdjnh.edu_app.repository;


import com.khangdjnh.edu_app.entity.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findAllByClassIdOrderByCreatedAtDesc(Long classId);

    List<Assignment> findByEndAtBetween(LocalDateTime from, LocalDateTime to);


    @Query("""
    SELECT a FROM Assignment a
    WHERE a.classEntity.id IN :classIds
    AND (:keyword IS NULL OR a.title LIKE %:keyword%)
    AND (:startFrom IS NULL OR a.startAt >= :startFrom)
    AND (:endTo IS NULL OR a.endAt <= :endTo)
""")
    Page<Assignment> searchAssignmentsForStudent(
            @Param("classIds") List<Long> classIds,
            @Param("keyword") String keyword,
            @Param("startFrom") LocalDateTime startFrom,
            @Param("endTo") LocalDateTime endTo,
            Pageable pageable
    );

}
