package com.demo.poli.api.chatbot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChatBotProgressResponse {

    @Schema(description = "진정서 작성 가능 여부 (80% 이상 시 true)", example = "false")
    private boolean fulfilled;
    @Schema(description = "전체 완성도 (0-100%)", example = "75")
    private double percentage;
}
