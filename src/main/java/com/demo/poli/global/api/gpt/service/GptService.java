package com.demo.poli.global.api.gpt.service;

import com.demo.poli.global.api.gpt.vo.GptRequest;
import com.demo.poli.global.api.gpt.vo.GptResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class GptService {

    private final WebClient clovaWebClient;
    private final ObjectMapper objectMapper;


    public Flux<GptResponse> getChatCompletion(GptRequest request) {

        log.info("request : {}", request);
        return clovaWebClient.post()
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatusCode::isError, clientResponse -> {
                log.info("clientResponse : {}", clientResponse);
                var stringMono = clientResponse.bodyToMono(String.class);
                log.info("stringMono : {}", stringMono);
                return Mono.error(new RuntimeException(clientResponse.toString()));
            })
            .bodyToFlux(String.class)
            .doOnNext(response -> log.info("response : {}", response))
            .map(response -> {
                try {
                    return objectMapper.readValue(response, GptResponse.class);
                } catch (JsonProcessingException e) {
                    log.error("", e);
                    return new GptResponse();
                }
            });
    }

}
