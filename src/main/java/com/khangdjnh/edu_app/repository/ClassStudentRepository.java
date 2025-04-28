package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.ClassStudent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {

    boolean existsByClassEntity_Id(Long classId);

    boolean existsByStudent_Id(Long studentId);

    List<ClassStudent> findByClassEntity_Id(Long classId);

    List<ClassStudent> findByStudent_Id(Long studentId);
}