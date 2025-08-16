package com.demo.poli.chat.service;

import com.demo.poli.chat.entity.ChatPetitionEntity;
import com.demo.poli.chat.entity.ChatRoomEntity;
import com.demo.poli.chat.repository.ChatPetitionRepository;
import com.demo.poli.global.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatPetitionService {


    private final ChatPetitionRepository chatPetitionRepository;

    public ChatPetitionEntity getChatPetition(ChatRoomEntity chatRoom) {
        return chatPetitionRepository.getByChatRoomAndLatestTrue(chatRoom)
            .orElseThrow(() -> new BaseException("Chat petition not found for chat room: " + chatRoom.getId()));
    }


    @Transactional
    public ChatPetitionEntity updatePetition(ChatRoomEntity chatRoom, String petitionJson) {
        chatPetitionRepository.updateLatestFalse(chatRoom.getId());

        var chatPetition = ChatPetitionEntity.builder()
            .chatRoom(chatRoom)
            .petitionJson(petitionJson)
            .latest(true)
            .build();

        return chatPetitionRepository.save(chatPetition);

    }
}
