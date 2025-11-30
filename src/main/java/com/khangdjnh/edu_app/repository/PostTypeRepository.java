package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.PostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTypeRepository extends JpaRepository<PostType, Long> {
}
