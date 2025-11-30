package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE m.conversationId = :cid  AND (:cursor IS NULL OR m.id < :cursor) ORDER BY m.id ASC ")
    List<Message> loadMessages(
            @Param("cid") Long conversationId,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

}
