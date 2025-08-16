package com.demo.poli.api.s3.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.demo.poli.api.s3.vo.S3DownloadResponse;
import com.demo.poli.api.s3.vo.S3ObjectInfo;
import com.demo.poli.global.config.PoliConfig;
import com.demo.poli.global.exception.BaseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;
    private final PoliConfig.S3 s3PoliConfig;

    public static final String METADATA_ORIGINAL_FILENAME = "original-filename";
    public static final String METADATA_UPLOAD_AT = "upload-at";
    public static final Duration PRESIGNED_URL_DURATION = Duration.ofDays(7);

    /**
     * 파일 업로드
     */
    public S3ObjectInfo uploadFile(MultipartFile file, String folder) {
        var key = generateKey(folder, file.getOriginalFilename());

        try {
            var contentType = determineContentType(file);

            log.info("Naver Object Storage 파일 업로드 시작 - Key: {}, Size: {} bytes", key, file.getSize());

            // 메타데이터 설정
            var metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(file.getSize());
            metadata.addUserMetadata(METADATA_ORIGINAL_FILENAME, file.getOriginalFilename());
            metadata.addUserMetadata(METADATA_UPLOAD_AT, LocalDateTime.now().toString());

            // 파일 업로드
            var putObjectRequest = new PutObjectRequest(
                s3PoliConfig.getBucket(),
                key,
                file.getInputStream(),
                metadata
            );

            // Naver Object Storage에 파일 업로드
            s3Client.putObject(putObjectRequest);
            return getFileInfo(key, metadata);
        } catch (Exception e) {
            log.error("파일 업로드 실패 ", e);

            throw new BaseException("파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }


    public AccessControlList getBucket() {
        var bucketName = s3PoliConfig.getBucket();
        return s3Client.getBucketAcl(bucketName);
    }

    /**
     * 다중 파일 업로드
     */
    public List<S3ObjectInfo> uploadFiles(List<MultipartFile> files, String folder) {
        var start = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(files)) {
            return List.of();
        }
        validateFiles(files);

        var fileKeys = files.parallelStream()
            .map(file -> uploadFile(file, folder))
            .toList();
        var end = System.currentTimeMillis();
        log.info("Naver Object Storage 다중 파일 업로드 성공 - 파일 수: {}, 소요 시간: {} ms", fileKeys.size(), (end - start));
        return fileKeys;
    }

    public S3ObjectInfo getFileInfo(String key) {
        var metadata = s3Client.getObjectMetadata(s3PoliConfig.getBucket(), key);
        return getFileInfo(key, metadata);
    }


    /**
     * 파일 정보 조회
     */
    public S3ObjectInfo getFileInfo(String key, ObjectMetadata metadata) {
        try {
            return S3ObjectInfo.builder()
                .key(key)
                .size(metadata.getContentLength())
                .eTag(metadata.getETag())
                .fileUrl(generatePresignedUrl(key))
                .originalFileName(metadata.getUserMetaDataOf(METADATA_ORIGINAL_FILENAME))
                .uploadAt(LocalDateTime.parse(metadata.getUserMetaDataOf(METADATA_UPLOAD_AT)))
                .build();

        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                throw new BaseException("파일을 찾을 수 없습니다: " + key);
            }
            throw new BaseException("파일 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }


    /**
     * Presigned URL 생성 (임시 다운로드 링크)
     */
    private String generatePresignedUrl(String key) {
        try {
            var generatePresignedUrlRequest = new GeneratePresignedUrlRequest(
                s3PoliConfig.getBucket(), key)
                .withMethod(HttpMethod.GET)
                .withExpiration(new Date(System.currentTimeMillis() + PRESIGNED_URL_DURATION.toMillis()));

            return s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
        } catch (Exception e) {
            log.error("Presigned URL 생성 실패 - Key: {}", key, e);
            throw new BaseException("Presigned URL 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 파일 다운로드
     */
    public S3DownloadResponse downloadFile(String key) {
        try {
            var s3Object = s3Client.getObject(s3PoliConfig.getBucket(), key);
            ObjectMetadata metadata = s3Object.getObjectMetadata();

            return S3DownloadResponse.builder()
                .key(key)
                .contentType(metadata.getContentType())
                .contentLength(metadata.getContentLength())
                .inputStream(s3Object.getObjectContent())
                .metadata(metadata.getUserMetadata())
                .build();

        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                log.error("파일을 찾을 수 없습니다 - Key: {}", key);
                throw new BaseException("파일을 찾을 수 없습니다: " + key);
            }
            log.error("파일 다운로드 실패 - Key: {}", key, e);
            throw new BaseException("파일 다운로드에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 파일 삭제
     */
    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(s3PoliConfig.getBucket(), key);
            log.info("파일 삭제 성공 - Key: {}", key);

        } catch (Exception e) {
            log.error("파일 삭제 실패 - Key: {}", key, e);
            throw new BaseException("파일 삭제에 실패했습니다: " + e.getMessage());
        }
    }


    private void validateFiles(List<MultipartFile> files) {
        if (files.stream().anyMatch(file -> file == null || file.isEmpty())) {
            throw new BaseException("파일이 비어있습니다.");
        }

        var totalSize = files.stream().map(MultipartFile::getSize)
            .reduce(0L, Long::sum);

        // Naver Object Storage 제한사항
        if (totalSize > 400L * 1024 * 1024) { // 400MB 제한
            throw new BaseException("파일 크기가 너무 큽니다. (최대 400MB)");
        }
    }


    private String generateKey(String folder, String originalFilename) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String extension = getFileExtension(originalFilename);

        String fileName = String.format("%s_%s.%s", timestamp, uuid, extension);

        return folder != null && !folder.isEmpty()
            ? folder + "/" + fileName
            : fileName;
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "unknown";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private String determineContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && !contentType.isEmpty()) {
            return contentType;
        }

        String extension = getFileExtension(file.getOriginalFilename());
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "pdf" -> "application/pdf";
            case "txt" -> "text/plain";
            case "json" -> "application/json";
            case "mp4" -> "video/mp4";
            case "mp3" -> "audio/mpeg";
            default -> "application/octet-stream";
        };
    }

}

