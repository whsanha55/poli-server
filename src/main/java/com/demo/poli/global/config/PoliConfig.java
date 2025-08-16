package com.demo.poli.global.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Slf4j
@ConfigurationProperties(prefix = "poli")
@Data
public class PoliConfig {

    private ChatBot chatBot;
    private S3 s3;

    @PostConstruct
    public void init() {
        log.info("PoliConfig : {}", this);
    }

    @Data
    public static class S3 {

        private String endPoint;
        private String region;
        private String bucket;
        private String accessKey;
        private String secretKey;
    }

    @Data
    public static class ChatBot {
        private String url;
    }
}
