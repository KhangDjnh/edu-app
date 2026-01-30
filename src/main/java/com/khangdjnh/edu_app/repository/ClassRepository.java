package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.ClassEntity;
import com.khangdjnh.edu_app.enums.ClassType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    boolean existsByCode(String code);

    List<ClassEntity> findAllByTeacherIdOrderByCreatedAtDesc(Long teacherId);
    List<ClassEntity> findByClassTypeOrderByCreatedAtDesc(ClassType classType);

    @Override
    Optional<ClassEntity> findById( Long id);
    Optional<ClassEntity> findByCode(String code);
}
