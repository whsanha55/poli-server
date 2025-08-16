package com.demo.poli.chat.vo;

import com.demo.poli.chat.entity.ChatPetitionEntity;
import com.demo.poli.global.base.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
public class ChatPetitionResponse extends BaseResponse {

    @Schema(description = "채팅방 아이디")
    String petitionJson;

    public ChatPetitionResponse(ChatPetitionEntity petition) {
        this.petitionJson = petition.getPetitionJson();
    }
}
