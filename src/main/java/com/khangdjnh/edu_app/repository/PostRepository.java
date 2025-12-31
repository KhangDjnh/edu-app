package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.ClassPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<ClassPost, Long> {
    List<ClassPost> findByClassEntityIdOrderByCreatedAtAsc(Long classId);
}
