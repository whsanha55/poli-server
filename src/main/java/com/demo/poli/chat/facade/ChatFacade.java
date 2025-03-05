package com.demo.poli.chat.facade;

import com.demo.poli.api.chatbot.service.ChatBotService;
import com.demo.poli.api.chatbot.vo.ChatBotRequest;
import com.demo.poli.api.gpt.service.GptService;
import com.demo.poli.api.gpt.vo.GptRequest;
import com.demo.poli.api.gpt.vo.GptRequest.GptMessage;
import com.demo.poli.api.gpt.vo.GptResponse;
import com.demo.poli.chat.entity.ChatMessageEntity;
import com.demo.poli.chat.enums.ChatRoleEnum;
import com.demo.poli.chat.service.ChatService;
import com.demo.poli.chat.vo.ChatRequest;
import com.demo.poli.chat.vo.ChatStreamResponse;
import com.demo.poli.global.exception.BaseException;
import io.micrometer.common.util.StringUtils;
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
    private final ChatBotService chatBotService;

    @Transactional
    public ChatMessageEntity newChat(ChatRequest request, String userId) {

        var chatRoom = Optional.ofNullable(request.getRoomId())
            .map(chatService::getRoom)
            .orElseGet(() -> chatService.createRoom(userId, request.getInitMessage(), request.getMessage()));

        // 요청 사용자 대화 추가
        return chatService.createChatMessage(chatRoom, request.getMessage(), ChatRoleEnum.USER);

    }

    @Transactional
    public Flux<ChatStreamResponse> chatStream(ChatRequest request, String userId) {
        var chatMessage = newChat(request, userId);
        var chatMessages = chatService.getChatMessagesOrderbyOld(chatMessage.getChatRoom().getId());

        var sb = new StringBuilder();
        return chatBotService.getChatCompletion(chatMessages)
            .doOnNext(response -> {
                    // ai 대화 결과 저장
                    var message = response.getLastMessage();
                    if (StringUtils.isNotEmpty(message)) {
                        sb.append(message);

                    }
                }
            )
            .publishOn(Schedulers.boundedElastic())
            .doAfterTerminate(() -> chatService.createChatMessage(chatMessage.getChatRoom(), sb.toString(), ChatRoleEnum.AI))
            .onErrorResume(BaseException.class, e -> {
                log.error("", e);
                return Flux.just(new ChatBotRequest());
            })
            .map(response -> new ChatStreamResponse(chatMessage, response.getLastMessage())
            );
    }

    public Flux<ChatStreamResponse> chatStream2(ChatRequest request, String userId) {
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
                    if (StringUtils.isNotEmpty(response.getResult())) {
                        sb.append(response.getResult());

                    }
                }
            )
            .doAfterTerminate(() -> chatService.createChatMessage(chatMessage.getChatRoom(), sb.toString(), ChatRoleEnum.AI))
            .onErrorResume(BaseException.class, e -> {
                log.error("", e);
                return Flux.just(new GptResponse());
            })
            .map(response -> new ChatStreamResponse(chatMessage, response.getResult())
            );
    }

}
