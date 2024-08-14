package com.demo.poli.chat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChatRequest {

    @Schema(description = "대화 카테고리 등 json", example = "{}", nullable = true)
    private String initMessage;

    @Schema(description = "채팅방 번호(기존 채팅방에서 지속적으로 대화하는 경우 입력)", example = "123", nullable = true)
    private Long roomId;
    @Schema(description = "대화 내용", example = "hello world")
    private String message;

}
