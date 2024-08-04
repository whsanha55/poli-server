package com.demo.poli.chat.facade;

import com.demo.poli.chat.enums.ChatRoleEnum;
import com.demo.poli.chat.service.ChatService;
import com.demo.poli.chat.vo.ChatRequest;
import com.demo.poli.chat.vo.ChatStreamResponse;
import com.demo.poli.entity.ChatMessageEntity;
import com.demo.poli.global.api.gpt.service.GptService;
import com.demo.poli.global.api.gpt.vo.GptRequest;
import com.demo.poli.global.api.gpt.vo.GptRequest.GptMessage;
import com.demo.poli.global.api.gpt.vo.GptResponse;
import com.demo.poli.global.exception.BaseException;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Slf4j
@Component
public class ChatFacade {


    private final ChatService chatService;
    private final GptService gptService;

    @Transactional
    public ChatMessageEntity newChat(ChatRequest request, String userId) {

        var chatRoom = Optional.ofNullable(request.getRoomId())
            .map(chatService::getRoom)
            .orElseGet(() -> chatService.createRoom(userId, request.getCategory(), request.getMessage()));

        // 요청 사용자 대화 추가
        return chatService.createChatMessage(chatRoom.getId(), request.getMessage(), ChatRoleEnum.USER);

    }

    public Flux<ChatStreamResponse> chatStream(ChatRequest request, String userId) {
        var chatMessage = newChat(request, userId);
        var sb = new StringBuilder();
        return gptService.getChatCompletion(
                GptRequest.builder()
                    .message(GptMessage.builder()
                        .role("user")
                        .content(chatMessage.getMessage())
                        .build())
                    .build())
            .publishOn(Schedulers.boundedElastic())
            .doOnNext(response -> { // ai 대화 결과 저장
                    log.info("response : {}", response);
                    sb.append(response.getResult());

                }
            )
            .doAfterTerminate(() -> chatService.createChatMessage(chatMessage.getChatRoomId(), sb.toString(), ChatRoleEnum.AI))
            .onErrorResume(BaseException.class, e -> {
                log.error("", e);
                return Flux.just(new GptResponse());
            })
            .map(response -> new ChatStreamResponse(chatMessage, response.getResult())
            );
    }

}
