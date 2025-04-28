package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByClassEntityId(Long classId);

    @Override
    Optional<Document> findById(Long id);
}
