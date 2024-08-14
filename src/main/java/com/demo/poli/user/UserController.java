package com.demo.poli.user;

import com.demo.poli.user.entity.UserEntity;
import com.demo.poli.user.service.UserService;
import com.demo.poli.user.vo.UserIdExistsResponse;
import com.demo.poli.user.vo.UserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserController", description = "사용자 정보를 관리합니다.")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 조회", description = """
        header 에 user-id : XXXX(sns 고유 id) 를 입력하면 인증으로 간주합니다. <br>
        """)
    @GetMapping("/user")
    public UserEntity getUser(String id) {
        return userService.getUser(id);
    }

    @Operation(summary = "사용자 등록", description = "사용자 정보를 등록합니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/user/sign-up")
    public void createUser(@Valid @RequestBody UserRequest request) {
        userService.createUser(request.toEntity());
    }

    @Operation(summary = "사용자 아이디 중복 체크")
    @PostMapping("/user/id/exists")
    public UserIdExistsResponse createUser(@RequestParam String userId) {
        var exist = userService.isExist(userId);
        return UserIdExistsResponse.builder()
            .userId(userId)
            .exists(exist)
            .build();

    }


}
