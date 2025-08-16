package com.demo.poli.chat.facade;

import com.demo.poli.api.chatbot.service.ChatBotService;
import com.demo.poli.api.chatbot.vo.ChatBotProgressResponse;
import com.demo.poli.api.chatbot.vo.ChatBotResponse;
import com.demo.poli.api.s3.service.S3Service;
import com.demo.poli.api.s3.vo.S3ObjectInfo;
import com.demo.poli.chat.entity.ChatMessageEntity;
import com.demo.poli.chat.enums.ChatRoleEnum;
import com.demo.poli.chat.service.ChatService;
import com.demo.poli.chat.vo.ChatRequest;
import com.demo.poli.chat.vo.ChatStreamResponse;
import com.demo.poli.global.exception.BaseException;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Slf4j
@Component
public class ChatFacade {


    private final ChatService chatService;
    private final ChatBotService chatBotService;
    private final S3Service s3Service;

    private ChatMessageEntity newChat(String userId, ChatRequest request, List<MultipartFile> files) {

        var chatRoom = Optional.ofNullable(request.getRoomId())
            .map(chatService::getRoom)
            .orElseGet(() -> chatService.createRoom(userId, request.getInitMessage(), request.getMessage()));

        // 파일 업로드
        var s3ObjectInfos = s3Service.uploadFiles(files, userId + "/" + chatRoom.getId());
        // 요청 사용자 대화 추가
        return chatService.createChatMessage(chatRoom, ChatRoleEnum.USER, request.getMessage(), s3ObjectInfos.toArray(new S3ObjectInfo[0]));

    }

    @Transactional
    public Flux<ChatStreamResponse> chatStream(String userId, ChatRequest request, List<MultipartFile> files) {
        var chatMessage = newChat(userId, request, files);

        var chatRoom = chatMessage.getChatRoom();

        return chatBotService.getChatCompletion(chatMessage)
            .publishOn(Schedulers.boundedElastic())
            .doOnNext(response -> {
                    var message = response.getFinalContent();
                    if (StringUtils.isNotEmpty(message)) {  // 마지막 메시지
                        log.info("chatStream request : {}", request);
                        log.info("chatStream response : {}", message);

                        // ai 대화 결과 저장
                        chatService.createChatMessage(chatRoom, ChatRoleEnum.AI, message);

                        // 채팅방 제목 업데이트
                        var sessionId = response.getSessionId();
                        var chatSummary = chatBotService.getChatSummary(sessionId);
                        chatService.updateRoomSessionId(chatRoom, sessionId, chatSummary);

                    }
                }
            )
            .doAfterTerminate(() -> {
                // todo
            })
            .onErrorResume(BaseException.class, e -> {
                log.error("", e);
                return Flux.just(new ChatBotResponse());
            })
            .map(response -> new ChatStreamResponse(chatMessage, response.getData())
            );
    }

    public ChatBotProgressResponse getChatProgress(Long chatRoomId) {
        var chatRoom = chatService.getRoom(chatRoomId);
        return chatBotService.getChatProgress(chatRoom.getSessionId());
    }

}
