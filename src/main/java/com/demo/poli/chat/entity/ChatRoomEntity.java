package com.demo.poli.chat.entity;

import com.demo.poli.global.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity(name = "ChatRoom")
@Builder
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, length = 256)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(length = 100)
    private String roomName;

    @Column(nullable = false)
    @Builder.Default
    private String initMessage = "{}";

    @OneToMany(mappedBy = "chatRoomId")
    private List<ChatMessageEntity> chatMessages;

    public void updateName(String roomName) {
        this.roomName = roomName;
    }
}
