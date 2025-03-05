package com.demo.poli.api.chatbot.vo;

import com.demo.poli.chat.entity.ChatMessageEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
@JsonInclude(Include.NON_DEFAULT)
public class ChatBotRequest {

    private List<GptMessage> messages = new ArrayList<>();
    @JsonProperty("session_id")
    private String sessionId;

    @Builder
    @Data
    public static class GptMessage {

        private String role;
        private String content;
    }


    public ChatBotRequest(List<ChatMessageEntity> entities) {
        var chatRoom = entities.get(0).getChatRoom();
        this.sessionId = chatRoom.getId().toString();

        // init message 입력
        messages.add(GptMessage.builder()
            .role("INIT")
            .content(chatRoom.getInitMessage())
            .build());

        // 대화 내용 입력
        this.messages.addAll(entities.stream()
            .map(message -> GptMessage.builder()
                .role(message.getRole().name())
                .content(message.getMessage())
                .build())
            .toList());
    }

    public String getLastMessage() {
        return Optional.ofNullable(CollectionUtils.lastElement(messages))
            .map(GptMessage::getContent)
            .orElse(null);
    }

}
