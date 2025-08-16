package com.demo.poli.api.chatbot.config;

import com.demo.poli.global.config.PoliConfig;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
@RequiredArgsConstructor
@Data
@Slf4j
public class ChatbotConfig {

    private final PoliConfig poliConfig;


    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl(poliConfig.getChatBot().getUrl())
            .build();
    }
}
