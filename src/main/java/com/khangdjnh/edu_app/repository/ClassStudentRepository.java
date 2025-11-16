package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.ClassStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {

    boolean existsByClassEntity_Id(Long classId);

    boolean existsByStudent_Id(Long studentId);

    ClassStudent findByClassEntity_IdAndStudent_Id(Long classId, Long studentId);

    @Query("SELECT sc.classEntity.id FROM ClassStudent sc WHERE sc.student.id = :studentId")
    List<Long> findClassIdsByStudentId(@Param("studentId") Long studentId);

    List<ClassStudent> findByClassEntity_Id(Long classId);

    List<ClassStudent> findByStudent_IdOrderByJoinAtDesc(Long studentId);
}