package com.demo.poli.chat.repository;

import com.demo.poli.chat.entity.ChatMessageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    @Query("""
            select cum
            from ChatMessage cum
            join cum.chatRoom cr
            where cum.chatRoomId = :chatRoomId
            and cr.deleted = false
            order by cum.createdAt desc
        """)
    List<ChatMessageEntity> findByChatRoomId(Long chatRoomId);

    @Query("""
            select cum
            from ChatMessage cum
            join cum.chatRoom cr
            where cr.userId = :userId
            and cr.deleted = false
            and cum.deleted = false
            and cum.bookmarked = true
            order by cum.createdAt desc
        """)
    List<ChatMessageEntity> findBookmarked(String userId);
}
