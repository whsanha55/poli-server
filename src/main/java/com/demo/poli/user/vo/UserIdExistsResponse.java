package com.demo.poli.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class UserIdExistsResponse {

    @Schema(description = "사용자 아이디", example = "user-id")
    String userId;
    @Schema(description = "존재 여부", example = "true")
    boolean exists;

}
