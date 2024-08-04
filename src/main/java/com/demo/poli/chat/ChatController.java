package com.demo.poli.chat;

import com.demo.poli.chat.facade.ChatFacade;
import com.demo.poli.chat.service.ChatService;
import com.demo.poli.chat.vo.ChatBookMarkResponse;
import com.demo.poli.chat.vo.ChatMessageResponse;
import com.demo.poli.chat.vo.ChatRequest;
import com.demo.poli.chat.vo.ChatRoomResponse;
import com.demo.poli.chat.vo.ChatStreamResponse;
import com.demo.poli.global.base.BaseResponse;
import com.demo.poli.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Tag(name = "ChatController", description = "채팅방을 관리합니다.")
@RequiredArgsConstructor
@RestController
@Slf4j
public class ChatController {

    private final UserService userService;
    private final ChatFacade chatFacade;
    private final ChatService chatService;

    @Operation(summary = "ai 실시간 대화", description = "사용자 대화를 통해 AI 대화를 생성하여 sse 반환")
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatStreamResponse> chatStream(@RequestHeader("user-id") String userId, @RequestBody ChatRequest request) {
        userService.getUser(userId);
        return chatFacade.chatStream(request, userId);
    }

    @Operation(summary = "채팅방 목록 조회", description = "최신순으로 나열")
    @GetMapping("/chat/rooms")
    public ChatRoomResponse getChatRooms(@RequestHeader("user-id") String userId) {
        userService.getUser(userId);
        return new ChatRoomResponse(chatService.getChatRooms(userId));
    }

    @Operation(summary = "채팅방 대화 조회", description = "최신순으로 나열 , 페이징 미구현(해야하나...ㅠ)")
    @GetMapping("/chat/messages/{roomId}")
    public ChatMessageResponse getChatMessages(@RequestHeader("user-id") String userId, @PathVariable Long roomId) {
        userService.getUser(userId);
        return new ChatMessageResponse(chatService.getChatMessages(roomId));
    }

    @Operation(summary = "채팅 북마크 조회", description = "최신순으로 나열 , 페이징 미구현(해야하나...ㅠ)")
    @GetMapping("/chat/bookmark")
    public ChatBookMarkResponse getChatMessages(@RequestHeader("user-id") String userId) {
        userService.getUser(userId);
        var chatMessages = chatService.findBookMarkedChatAiMessages(userId);
        return new ChatBookMarkResponse(chatMessages);
    }

    @Operation(summary = "ai 모자 채팅 내역 북마크", description = "스위치 형식으로 북마크 설정/해제")
    @PutMapping("/chat/bookmark/{chatMessageId}")
    public BaseResponse bookmarkChatMessage(@RequestHeader("user-id") String userId, @PathVariable Long chatMessageId) {
        userService.getUser(userId);
        var chatMessage = chatService.updateBookmark(chatMessageId);
        return new ChatBookMarkResponse(List.of(chatMessage));
    }


    @Operation(summary = "채팅방 삭제", description = "채팅방 삭제")
    @DeleteMapping("/chat/rooms/{roomId}")
    public BaseResponse deleteChatRoom(@RequestHeader("user-id") String userId, @PathVariable Long roomId) {
        userService.getUser(userId);
        chatService.deleteRoom(roomId);
        return new BaseResponse();
    }

}
