package com.demo.poli.chat.entity;

import com.demo.poli.api.s3.service.S3Service;
import com.demo.poli.api.s3.vo.S3ObjectInfo;
import com.demo.poli.chat.enums.ChatRoleEnum;
import com.demo.poli.global.base.BaseEntity;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Enumerated(EnumType.STRING)
    private ChatRoleEnum role;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean bookmarked;

    @Column
    private LocalDateTime bookmarkedAt;


    @ManyToOne(targetEntity = ChatRoomEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoomId")
    private ChatRoomEntity chatRoom;

    @OneToMany(mappedBy = "chatMessage", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChatImageEntity> chatImages;

    public void updateBookMark() {
        this.bookmarked = !this.bookmarked;
        this.bookmarkedAt = LocalDateTime.now();
    }

    public void setS3ObjectInfos(List<S3ObjectInfo> list) {
        if (this.chatImages == null) {
            this.chatImages = new ArrayList<>();
        }

        chatImages.addAll(
            list.stream().map(s3ObjectInfo -> {
                var now = LocalDateTime.now();
                ChatImageEntity chatImage = ChatImageEntity.builder()
                    .chatMessage(this)
                    .s3Key(s3ObjectInfo.getKey())
                    .size(s3ObjectInfo.getSize())
                    .fileUrl(s3ObjectInfo.getFileUrl())
                    .originalFileName(s3ObjectInfo.getOriginalFileName())
                    .uploadAt(now)
                    .urlExpiresAt(now.plus(S3Service.PRESIGNED_URL_DURATION))
                    .build();
                this.chatImages.add(chatImage);
                return chatImage;
            }).toList());
    }
}
