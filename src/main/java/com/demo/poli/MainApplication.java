package com.demo.poli;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationPropertiesScan
@EnableConfigurationProperties
@SpringBootApplication
@OpenAPIDefinition(
    servers = {
        @Server(url = "/", description = "Default Server URL")
    }
)

public class MainApplication {

    static {
        // AWS SDK v1 경고 메시지 비활성화
        System.setProperty("aws.java.v1.disableDeprecationAnnouncement", "true");
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);

    }

}
