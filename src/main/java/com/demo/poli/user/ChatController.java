package com.demo.poli.user;

import com.demo.poli.global.config.WebclientConfig;
import com.demo.poli.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ChatController {

    private final UserService userService;
    private final WebclientConfig webclientConfig;

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getUser(String content) {
        Flux<String> chatCompletion = webclientConfig.getChatCompletion(content);
//         return chatCompletion.collectList() // Flux를 List로 수집
//            .doOnNext(list -> log.info("s : {}", list)) // 수집된 List를 로그로 출력
//            .flatMapMany(Flux::fromIterable); // 다시 Flux로 변환
        return chatCompletion.doOnNext(s -> log.info("s : {}", s));

//        chatCompletion.collectList().flatMap(str -> {
//            var join = StringUtils.join(str);
//            log.info("join : {}", join);
//            return Mono.just(join);
//        }).subscribe();
//        return chatCompletion;
    }
}
