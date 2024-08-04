package com.demo.poli.global.api.gpt.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@ConfigurationProperties(prefix = "api.gpt")
@Configuration
@Data
@Slf4j
public class GptConfig {

    private String apiKey;
    private String model;
    private String url;


    @PostConstruct
    public void init() {
        log.info("this : {}", this);
    }

    @Bean
    public WebClient gptClient() {
        return WebClient.builder()
            .baseUrl(url)
            .defaultHeader("Content-Type", "application/json")
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .build();
    }
}
