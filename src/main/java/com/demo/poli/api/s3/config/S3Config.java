package com.demo.poli.api.s3.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.demo.poli.global.config.PoliConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final PoliConfig poliConfig;

    @Bean
    public AmazonS3 s3Client() {
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setSignerOverride("S3SignerType");  // 서명 방식 명시
        return AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(new EndpointConfiguration(
                poliConfig.getS3().getEndPoint(),
                poliConfig.getS3().getRegion()))
            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                poliConfig.getS3().getAccessKey(),
                poliConfig.getS3().getSecretKey())))
            .build();

    }

    @Bean
    public PoliConfig.S3 s3PoliConfig() {
        return poliConfig.getS3();
    }


}
