package com.demo.poli.global.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
@JsonInclude(Include.NON_DEFAULT)
public class HyperClovaRequest {

    private List<Message> messages;
    private double topP;
    private int topK;
    private int maxTokens;
    private double temperature;
    private double repeatPenalty;
    private List<String> stopBefore;
    private boolean includeAiFilters;
    private int seed;

    @Builder
    @AllArgsConstructor
    @Data
    public static class Message {

        private String role;
        private String content;
    }

}
