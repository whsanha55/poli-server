package com.demo.poli.chat.vo;

import com.demo.poli.chat.enums.ChatRoleEnum;
import com.demo.poli.chat.entity.ChatMessageEntity;
import com.demo.poli.global.base.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class ChatBookMarkResponse extends BaseResponse {

    List<ChatBookMark> bookMarks;

    @Value
    public static class ChatBookMark {

        @Schema(description = "채팅방 id", example = "123")
        Long roomId;
        @Schema(description = "채팅방 이름", example = "room name")
        String roomName;
        @Schema(description = "역할", example = "USER")
        ChatRoleEnum role;
        @Schema(description = "대화 내용", example = "hello world")
        String message;
        @Schema(description = "북마크 시간", example = "2021-07-01T00:00:00")
        LocalDateTime bookmarkedAt;

        public ChatBookMark(ChatMessageEntity entity) {
            this.roomId = entity.getChatRoomId();
            this.roomName = entity.getChatRoom().getRoomName();
            this.role = entity.getRole();
            this.message = entity.getMessage();
            this.bookmarkedAt = entity.getBookmarkedAt();
        }
    }

    public ChatBookMarkResponse(List<ChatMessageEntity> entities) {
        this.bookMarks = entities.stream()
            .map(ChatBookMark::new)
            .toList();
    }
}
