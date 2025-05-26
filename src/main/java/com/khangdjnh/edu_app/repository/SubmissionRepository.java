package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.Submission;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    @EntityGraph(attributePaths = "submissionFiles")
    Optional<Submission> findWithFilesById(Long id);

    @EntityGraph(attributePaths = "submissionFiles")
    List<Submission> findWithFilesByAssignmentId(Long assignmentId);

    @EntityGraph(attributePaths = "submissionFiles")
    Optional<Submission> findWithFilesByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    @Query("""
    SELECT s FROM Submission s
    JOIN FETCH s.submissionFiles
    WHERE s.student.id = :studentId
      AND s.assignment.classEntity.id = :classId
""")
    List<Submission> findWithFilesByStudentIdAndClassId(@Param("studentId") Long studentId,
                                                        @Param("classId") Long classId);
}
