package com.demo.poli.chat.service;

import com.demo.poli.api.s3.vo.S3ObjectInfo;
import com.demo.poli.chat.entity.ChatMessageEntity;
import com.demo.poli.chat.entity.ChatMessageEntity.ChatMessageEntityBuilder;
import com.demo.poli.chat.entity.ChatRoomEntity;
import com.demo.poli.chat.enums.ChatRoleEnum;
import com.demo.poli.chat.repository.ChatMessageRepository;
import com.demo.poli.chat.repository.ChatRoomRepository;
import com.demo.poli.global.exception.BaseException;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatService {


    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatRoomEntity createRoom(String userId, String initMessage, String message) {
        return chatRoomRepository.save(ChatRoomEntity.builder()
            .userId(userId)
            .initMessage(initMessage)
            .roomName(StringUtils.substring(message, 0, 100))
            .build());
    }

    @Transactional
    public ChatMessageEntity createChatMessage(ChatRoomEntity chatRoom, ChatRoleEnum role, String message, S3ObjectInfo... s3ObjectInfos) {
        var chatMessage = ChatMessageEntity.builder()
            .chatRoom(chatRoom)
            .role(role)
            .message(message)
            .build();

        if (s3ObjectInfos.length > 0) {
            chatMessage.setS3ObjectInfos(Arrays.asList(s3ObjectInfos));
        }

        return chatMessageRepository.save(chatMessage);
    }

    public ChatRoomEntity getRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new BaseException("ChatRoom not found"));
    }

    public List<ChatRoomEntity> getChatRooms(String userId) {
        return chatRoomRepository.findByUserIdOrderByIdDesc(userId);
    }


    public List<ChatMessageEntity> getChatMessages(Long chatRoomId) {
        return chatMessageRepository.findByChatRoomId(chatRoomId);
    }

    public List<ChatMessageEntity> getChatMessagesOrderbyOld(Long chatRoomId) {
        return chatMessageRepository.findByChatRoomIdOrderByCreatedAt(chatRoomId);
    }

    public ChatMessageEntity getChatMessage(Long aiMessageId) {
        return chatMessageRepository.findById(aiMessageId)
            .orElseThrow(() -> new BaseException("ChatAiMessage not found"));
    }

    public List<ChatMessageEntity> getChatUserMessages(Long chatRoomId) {
        return chatMessageRepository.findByChatRoomId(chatRoomId);
    }

    public List<ChatMessageEntity> findBookMarkedChatAiMessages(String userId) {
        return chatMessageRepository.findBookmarked(userId);
    }

    @Transactional
    public ChatMessageEntity updateBookmark(Long chatMessageId) {
        var chatMessage = getChatMessage(chatMessageId);
        chatMessage.updateBookMark();
        return chatMessageRepository.save(chatMessage);
    }

    @Transactional
    public void deleteRoom(Long chatRoomId) {
        var chatRoom = getRoom(chatRoomId);
        chatRoom.delete();
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void updateRoomSessionId(ChatRoomEntity chatRoom, String sessionId, String chatSummary) {
        chatRoom.setSessionId(sessionId);
        if (StringUtils.isNotEmpty(chatSummary)) {
            chatRoom.updateName(chatSummary);
        }
        chatRoomRepository.save(chatRoom);

    }
}
