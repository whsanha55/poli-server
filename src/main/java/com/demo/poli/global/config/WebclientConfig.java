package com.demo.poli.global.config;

import com.demo.poli.global.config.HyperClovaRequest.Message;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Primary
@RequiredArgsConstructor
@Configuration
@Slf4j
public class WebclientConfig {


    private final WebClient webClient = WebClient.builder()
        .baseUrl("https://clovastudio.stream.ntruss.com")
        .defaultHeader("X-NCP-CLOVASTUDIO-API-KEY", "NTA0MjU2MWZlZTcxNDJiY6MZCZJAkdCEmdN9igsR84TyR+ZXKgJ4laa8qTRZWRqC")
        .defaultHeader("X-NCP-APIGW-API-KEY", "iuhDA9Q6Lm7gKajHypYCifOW3nzEqVDoiw8MjDzO")
        .defaultHeader("X-NCP-CLOVASTUDIO-REQUEST-ID", "7c4c3d21-2583-4beb-afba-77e071a1eb5d")
        .defaultHeader("Content-Type", "application/json")
        .defaultHeader("Accept", "text/event-stream")
        .build();

    public Flux<String> getChatCompletion(String content) {
        // RequestBody 객체 생성
        var body = HyperClovaRequest.builder()
            .messages(Arrays.asList(
//                Message.builder()
//                .role("system")
//                .content("")
//                .build(),
                Message.builder()
                    .role("user")
                    .content(content)
                    .build()))
//            .topP(0.8)
//            .topK(0)
//            .maxTokens(256)
//            .temperature(0.5)
//            .repeatPenalty(5.0)
//            .stopBefore(List.of())
//            .includeAiFilters(true)
//            .seed(0)
            .build();
        return webClient.post()
            .uri("/testapp/v1/chat-completions/HCX-DASH-001")
            .bodyValue(body)
            .retrieve()
            .onStatus(HttpStatusCode::isError, clientResponse -> {
                log.info("clientResponse : {}", clientResponse);
                var stringMono = clientResponse.bodyToMono(String.class);
                log.info("stringMono : {}", stringMono);
                return Mono.error(new RuntimeException(clientResponse.toString()));
            })
            .bodyToFlux(String.class);
    }

}
