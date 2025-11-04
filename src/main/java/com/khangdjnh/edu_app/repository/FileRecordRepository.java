package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {
}
