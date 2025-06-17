package com.demo.poli.api.chatbot.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
@JsonInclude(Include.NON_DEFAULT)
public class ChatBotResponse {

    @JsonProperty("session_id")
    private String sessionId;
    @JsonProperty("event_type")
    private String eventType;
    private String data;
    @JsonProperty("agent_name")
    private String agentName;
    @JsonProperty("tool_output")
    private String toolOutput;
    @JsonProperty("message_output")
    private String messageOutput;
    @JsonProperty("final_content")
    private String finalContent;
}
