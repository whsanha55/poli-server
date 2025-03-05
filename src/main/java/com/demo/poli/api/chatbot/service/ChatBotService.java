package com.demo.poli.api.chatbot.service;

import com.demo.poli.api.chatbot.config.ChatbotConfig;
import com.demo.poli.api.chatbot.vo.ChatBotRequest;
import com.demo.poli.chat.entity.ChatMessageEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatBotService {

    private final ChatbotConfig chatbotConfig;

    public Flux<ChatBotRequest> getChatCompletion(List<ChatMessageEntity> entity) {

        var chatBotRequest = new ChatBotRequest(entity);
        log.info("chatBotRequest : {}", chatBotRequest);
        return chatbotConfig.webClient().post()
            .uri("/api/v1/chat")
            .bodyValue(chatBotRequest)
            .retrieve()
            .onStatus(HttpStatusCode::isError, clientResponse -> {
                log.info("clientResponse : {}", clientResponse);
                var stringMono = clientResponse.bodyToMono(String.class);
                log.info("stringMono : {}", stringMono);
                return Mono.error(new RuntimeException(clientResponse.toString()));
            })
            .bodyToMono(ChatBotRequest.class)
            .doOnNext(response -> log.info("chat bot response : {}", response))
            .flux();
    }

}
