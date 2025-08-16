package com.demo.poli.api.s3.vo;

import java.io.InputStream;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class S3DownloadResponse {

    private String key;
    private String contentType;
    private Long contentLength;
    private InputStream inputStream;
    private Map<String, String> metadata;
}
