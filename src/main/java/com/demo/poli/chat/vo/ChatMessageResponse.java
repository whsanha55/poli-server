package com.demo.poli.chat.vo;

import com.demo.poli.chat.enums.ChatRoleEnum;
import com.demo.poli.entity.ChatMessageEntity;
import com.demo.poli.global.base.BaseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class ChatMessageResponse extends BaseResponse {

    List<ChatUserMessage> rooms;

    public ChatMessageResponse(List<ChatMessageEntity> entities) {
        this.rooms = entities.stream()
            .map(ChatUserMessage::new)
            .toList();
    }

    @Value
    @JsonInclude(Include.NON_EMPTY)
    public static class ChatUserMessage {

        @Schema(description = "채팅방 번호", example = "123")
        Long roomId;
        @Schema(description = "역할", example = "USER")
        ChatRoleEnum role;
        @Schema(description = "유저 메세지", example = "hello world")
        String message;
        @Schema(description = "생성일", example = "2021-07-01T00:00:00.000000")
        LocalDateTime createdAt;

        public ChatUserMessage(ChatMessageEntity entity) {
            this.roomId = entity.getChatRoomId();
            this.role = entity.getRole();
            this.message = entity.getMessage();
            this.createdAt = entity.getCreatedAt();
        }
    }

}
