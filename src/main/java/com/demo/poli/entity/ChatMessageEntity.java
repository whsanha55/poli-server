package com.demo.poli.entity;

import com.demo.poli.chat.enums.ChatRoleEnum;
import com.demo.poli.global.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity(name = "ChatMessage")
@Builder
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, length = 256)
    private Long id;

    private Long chatRoomId;

    @Enumerated(EnumType.STRING)
    private ChatRoleEnum role;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean bookmarked;

    @Column
    private LocalDateTime bookmarkedAt;


    @ManyToOne(targetEntity = ChatRoomEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoomId", insertable = false, updatable = false)
    private ChatRoomEntity chatRoom;

    public void updateBookMark() {
        this.bookmarked = !this.bookmarked;
        this.bookmarkedAt = LocalDateTime.now();
    }
}
