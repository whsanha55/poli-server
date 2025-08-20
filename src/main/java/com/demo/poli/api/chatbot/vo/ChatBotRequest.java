package com.demo.poli.api.chatbot.vo;

import com.demo.poli.chat.entity.ChatImageEntity;
import com.demo.poli.chat.entity.ChatMessageEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
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
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("image_url")
    private List<String> imageUrl;

    public ChatBotRequest(ChatMessageEntity entity) {
        var chatRoom = entity.getChatRoom();
        this.sessionId = chatRoom.getSessionId();
        this.userId = entity.getUser().getUserName();
        this.userName = entity.getUser().getUserName();
        this.message = entity.getMessage();
        // 초기 대화가 없는 경우, 초기 메시지 추가
        if (sessionId == null) {
            this.message = chatRoom.getInitMessage() + this.message;
        }

        if (entity.getChatImages() != null) {
            this.imageUrl = entity.getChatImages()
                .stream()
                .map(ChatImageEntity::getFileUrl)
                .toList();
        }


    }

}
