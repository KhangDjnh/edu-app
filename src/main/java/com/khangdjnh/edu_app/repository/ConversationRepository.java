package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.Conversation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT c from Conversation c WHERE (c.firstUser = :uid OR c.secondUser = :uid) AND c.isActive = true AND (:cursor IS NULL OR c.id < :cursor) ORDER BY c.updatedAt DESC")
    List<Conversation> loadConversation(
            @Param("uid") Long userId,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

    Conversation findById(long id);

    @Query("SELECT c from Conversation c WHERE c.isActive = true AND (((c.firstUser = :fir) AND (c.secondUser = :sec)) OR ((c.firstUser = :sec) AND (c.secondUser = :fir)))")
    Conversation findByFirstUserIdAndSecondUserId(
            @Param("fir") Long firstUserId,
            @Param("sec") Long secondUserId
    );
}
