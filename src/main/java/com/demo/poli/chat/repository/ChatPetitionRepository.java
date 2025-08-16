package com.demo.poli.chat.repository;

import com.demo.poli.chat.entity.ChatPetitionEntity;
import com.demo.poli.chat.entity.ChatRoomEntity;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ChatPetitionRepository extends JpaRepository<ChatPetitionEntity, Long> {


    @Query("""
            SELECT cp
            FROM ChatPetition cp
            WHERE cp.chatRoom = :chatRoom
            AND cp.latest = true
        """)
    Optional<ChatPetitionEntity> getByChatRoomAndLatestTrue(ChatRoomEntity chatRoom);

    @Transactional
    @Modifying
    @Query("""
            UPDATE ChatPetition cp
            SET cp.latest = false
            WHERE cp.chatRoom.id = :chatRoomId
        """)
    void updateLatestFalse(Long chatRoomId);


}
