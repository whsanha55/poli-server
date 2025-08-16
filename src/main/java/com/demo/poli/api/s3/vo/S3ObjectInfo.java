package com.demo.poli.api.s3.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class S3ObjectInfo {

    private String key;
    private Long size;
    private String eTag;
    private String fileUrl;
    private String originalFileName;
    private LocalDateTime uploadAt;

}
