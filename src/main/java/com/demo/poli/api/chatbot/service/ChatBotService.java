package com.demo.poli.api.chatbot.service;

import com.demo.poli.api.chatbot.config.ChatbotConfig;
import com.demo.poli.api.chatbot.vo.ChatBotProgressResponse;
import com.demo.poli.api.chatbot.vo.ChatBotRequest;
import com.demo.poli.api.chatbot.vo.ChatBotResponse;
import com.demo.poli.chat.entity.ChatMessageEntity;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatBotService {

    private final ChatbotConfig chatbotConfig;

    public Flux<ChatBotResponse> getChatCompletion(ChatMessageEntity entity) {

        var chatBotRequest = new ChatBotRequest(entity);
        log.info("chatBotRequest : {}", chatBotRequest);
        return chatbotConfig.webClient().post()
            .uri("/v1/chat/stream")
            .bodyValue(chatBotRequest)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorMessage())
            .bodyToFlux(ChatBotResponse.class)
            .doOnNext(response -> log.info("chat bot response : {}", response));
    }

    public String getChatSummary(String sessionId) {
        return chatbotConfig.webClient().post()
            .uri("/v1/summary")
            .bodyValue(Map.of("session_id", sessionId))
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorMessage())
            .bodyToMono(Map.class)
            .doOnNext(response -> log.info("chat bot summary response : {}", response))
            .block()
            .getOrDefault("summary", "")
            .toString();

    }

    public ChatBotProgressResponse getChatProgress(String sessionId) {
        return chatbotConfig.webClient().post()
            .uri("/v1/petition/readiness")
            .bodyValue(Map.of("session_id", sessionId))
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorMessage())
            .bodyToMono(ChatBotProgressResponse.class)
            .doOnNext(response -> log.info("chat bot progress response : {}", response))
            .block();

    }

    private Function<ClientResponse, Mono<? extends Throwable>> errorMessage() {
        return clientResponse -> {
            log.info("clientResponse : {}", clientResponse);
            var stringMono = clientResponse.bodyToMono(String.class);
            log.info("stringMono : {}", stringMono);
            return Mono.error(new RuntimeException(clientResponse.toString()));
        };
    }

}
