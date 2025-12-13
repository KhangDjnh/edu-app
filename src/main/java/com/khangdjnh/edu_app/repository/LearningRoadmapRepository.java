package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.LearningRoadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningRoadmapRepository extends JpaRepository<LearningRoadmap, Long> {
    List<LearningRoadmap> findByClassEntity_Id(Long classId);
    int countByClassEntity_IdAndParentId(Long classId, Long parentId);
    List<LearningRoadmap> findByParentId(Long parentId);
}
