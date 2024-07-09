package com.demo.poli.global.config;

import com.demo.poli.global.config.vo.ChatRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Primary
@RequiredArgsConstructor
@Configuration
@Slf4j
public class WebclientConfig {


    //    @Value("${openai.key}")
    private String key = "sk-proj-Ctklc37OjaOOu6suGMqXT3BlbkFJPEOn9Vqb7sZvi6rBjShu";
    private final WebClient webClient = WebClient.builder()
        .baseUrl("https://api.openai.com/v1/chat/completions")
        .defaultHeader("Content-Type", "application/json")
        .defaultHeader("Authorization", "Bearer " + key)
        .build();

    public Mono<String> getChatCompletion(ChatRequest request) {
        log.info("key : {}", key);
        return webClient.post()
            .uri("")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(String.class)
            .onErrorResume(WebClientResponseException.class, ex -> {
                log.info("Error response: {}", ex.getResponseBodyAsString());
                return Mono.error(ex);
            });
    }

}
