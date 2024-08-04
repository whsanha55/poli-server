package com.demo.poli.global.api.gpt.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

@Builder
@AllArgsConstructor
@Data
@Slf4j
@JsonInclude(Include.NON_DEFAULT)
public class GptRequest {

    @Singular("message")
    private List<GptMessage> messages;

    @Builder.Default
    private String model = "gpt-4o-mini";

    @Builder.Default
    private boolean stream = true;


    @Builder
    @Data
    public static class GptMessage {

        private String role;
        private String content;
    }

}
