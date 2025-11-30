package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.EmotionCounter;
import com.khangdjnh.edu_app.enums.ParentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmotionCounterRepository extends JpaRepository<EmotionCounter, Long> {
    List<EmotionCounter> findByParentIdAndParentType(Long parentId, ParentType parentType);
}
