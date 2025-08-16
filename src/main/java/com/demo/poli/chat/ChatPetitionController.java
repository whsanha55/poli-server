package com.demo.poli.chat;

import com.demo.poli.api.chatbot.vo.ChatBotProgressResponse;
import com.demo.poli.chat.facade.ChatFacade;
import com.demo.poli.chat.vo.ChatPetitionRequest;
import com.demo.poli.chat.vo.ChatPetitionResponse;
import com.demo.poli.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

//@Tag(name = "ChatPetitionController", description = "진정서 조회 및 수정")
@RequiredArgsConstructor
@RestController
@Slf4j
public class ChatPetitionController {

    private final UserService userService;
    private final ChatFacade chatFacade;

    @Operation(summary = "채팅 진척도 조회")
    @GetMapping("/chat/progress/{roomId}")
    public ChatBotProgressResponse getChatProgress(@RequestHeader("user-id") String userId, @PathVariable Long roomId) {
        userService.getUser(userId);
        return chatFacade.getChatProgress(roomId);
    }


    @Operation(summary = "채팅 진정서 조회")
    @GetMapping("/chat/petition/{roomId}")
    public ChatPetitionResponse getChatPetition(@RequestHeader("user-id") String userId, @PathVariable Long roomId) {
        userService.getUser(userId);
        var chatPetition = chatFacade.getChatPetition(roomId);
        return new ChatPetitionResponse(chatPetition);
    }


    @Operation(summary = "채팅 진정서 변경", description = "진정서 json 또는 raw 마크업 중 하나만 제공해야 합니다.")
    @PutMapping("/chat/petition/{roomId}")
    public ChatPetitionResponse updateChatPetition(
        @RequestHeader("user-id") String userId,
        @PathVariable Long roomId,
        @Valid @RequestBody ChatPetitionRequest request) {
        userService.getUser(userId);
        var chatPetition = chatFacade.updateChatPetition(roomId, request);
        return new ChatPetitionResponse(chatPetition);
    }

}
