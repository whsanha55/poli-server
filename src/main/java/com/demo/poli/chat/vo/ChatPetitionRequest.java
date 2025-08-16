package com.demo.poli.chat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class ChatPetitionRequest {

    @Schema(description = "진정서 json", example = "{\"title\": \"Petition Title\", \"content\": \"Petition Content\"}")
    private String petitionJson;
    @Schema(description = "진정서 raw 마크업", example = "<p>Petition Raw Content</p>")
    private String petitionRaw;

    @AssertTrue(message = "진정서 json 또는 raw 마크업 중 하나만 제공해야 합니다.")
    public boolean isOnlyExistOne() {
        return (petitionJson != null && petitionRaw == null) || (petitionJson == null && petitionRaw != null);
    }
}
