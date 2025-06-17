package com.demo.poli.api.chatbot.vo;

import com.demo.poli.chat.entity.ChatMessageEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
@JsonInclude(Include.NON_DEFAULT)
public class ChatBotRequest {

    private String message;
    @JsonProperty("session_id")
    private String sessionId;

    @Builder
    @Data
    public static class GptMessage {

        private String role;
        private String content;
    }


    public ChatBotRequest(ChatMessageEntity entity) {
        var chatRoom = entity.getChatRoom();
        this.sessionId = chatRoom.getSessionId();
        this.message = entity.getMessage();
        // 초기 대화가 없는 경우, 초기 메시지 추가
        if (sessionId == null) {
            this.message = chatRoom.getInitMessage() + this.message;
        }

    }

}
