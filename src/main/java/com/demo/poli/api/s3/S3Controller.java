package com.demo.poli.api.s3;

import com.demo.poli.api.s3.service.S3Service;
import com.demo.poli.api.s3.vo.S3DownloadResponse;
import com.demo.poli.api.s3.vo.S3ObjectInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<S3ObjectInfo> uploadFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "folder", defaultValue = "uploads") String folder) {

        var response = s3Service.uploadFile(file, folder);
        log.info("File uploaded successfully: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<S3ObjectInfo>> uploadFiles(
        @RequestPart("files") List<MultipartFile> files,
        @RequestParam(value = "folder", defaultValue = "uploads") String folder) {

        var response = s3Service.uploadFiles(files, folder);
        log.info("Files uploaded successfully: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestBody String key) {
        S3DownloadResponse response = s3Service.downloadFile(key);

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(response.getContentType()))
            .contentLength(response.getContentLength())
            .body(new InputStreamResource(response.getInputStream()));
    }

    @PostMapping("/bucket")
    public Object bucket() {
        return s3Service.getBucket();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFile(@RequestBody String key) {
        s3Service.deleteFile(key);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/presigned-url")
    public ResponseEntity<S3ObjectInfo> generatePresignedUrl(
        @RequestBody String key) {

        var info = s3Service.getFileInfo(key);
        return ResponseEntity.ok(info);
    }

}
