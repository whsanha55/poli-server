package com.demo.poli.chat.service;

import com.demo.poli.chat.ChatController.ChatRequest;
import com.demo.poli.chat.entity.Chat;
import com.demo.poli.chat.entity.ChatContent;
import com.demo.poli.chat.repository.ChatContentRepository;
import com.demo.poli.chat.repository.ChatRepository;
import com.demo.poli.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatService {

    private final UserService userService;
    private final ChatRepository chatRepository;
    private final ChatContentRepository chatContentRepository;

    public Chat createChat(String userId, String subject) {
        userService.getUser(userId);
        return chatRepository.save(
            Chat.builder()
                .userId(userId)
                .subject(subject)
                .build());

    }

    public ChatContent addChatContent(ChatRequest request) {
        return chatContentRepository.save(request.toEntity());
    }

    public Chat getChat(String chatId) {
        return chatRepository.findById(chatId)
            .map(chat -> {
                chat.setContents(chatContentRepository.findByChatId(chatId));
                return chat;
            })
            .orElseThrow(() -> new RuntimeException("chat not found"));
    }
}
