package com.demo.poli.chat.vo;

import com.demo.poli.entity.ChatMessageEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class ChatStreamResponse {

    @Schema(description = "대화방 ID", example = "123")
    Long roomId;

    @Schema(description = "대화 내용", example = "hello world")
    String message;

    public ChatStreamResponse(ChatMessageEntity entity, String message) {
        this.roomId = entity.getChatRoomId();
        this.message = message;
    }

}
