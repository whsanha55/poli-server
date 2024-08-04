package com.demo.poli.global.api.gpt.vo;

import java.util.List;
import lombok.Data;

@Data
public class GptResponse {

    private String id;
    private String object;
    private long created;
    private String model;
    private String systemFingerprint;
    private List<Choice> choices;


    public String getResult() {
        try {
            return choices.get(0).getDelta().getContent();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return "";
        }
    }

    @Data
    public static class Choice {

        private int index;
        private Delta delta;
        private Object logprobs;
        private String finishReason;

        @Data
        public static class Delta {

            private String content;
        }
    }
}
