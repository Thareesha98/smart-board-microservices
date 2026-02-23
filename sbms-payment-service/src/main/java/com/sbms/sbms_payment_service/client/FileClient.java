package com.sbms.sbms_payment_service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Service
@Slf4j
public class FileClient {

    private final WebClient webClient;

    public FileClient(
            @Qualifier("fileServiceWebClient") WebClient webClient
    ) {
        this.webClient = webClient;
    }

    /**
     * Upload PDF bytes to sbms-backend FileController
     * Endpoint: POST /api/files/upload/{folder}
     * Form field: file (MultipartFile)
     */
    @CircuitBreaker(name = "fileService", fallbackMethod = "fallbackUpload")
    @Retry(name = "fileService")
    public String uploadBytes(byte[] bytes, String fileName, String folder) {

        if (bytes == null || bytes.length == 0) {
            log.warn("Skipping file upload: empty bytes");
            return null;
        }

        try {
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

            bodyBuilder.part(
                    "file",
                    new ByteArrayResource(bytes) {
                        @Override
                        public String getFilename() {
                            return fileName;
                        }
                    }
            ).contentType(MediaType.APPLICATION_PDF);

            // IMPORTANT: backend adds "/" automatically (folder + "/")
            String url = webClient.post()
                    .uri("/api/files/upload/{folder}", folder)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(bodyBuilder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            log.info("File uploaded successfully to folder={} url={}", folder, url);
            return url;

        } catch (Exception ex) {
            log.error("File upload failed for {}", fileName, ex);
            throw new RuntimeException("File upload service unavailable");
        }
    }

    public String fallbackUpload(byte[] bytes, String fileName, String folder, Throwable ex) {
        log.error("File service DOWN. Fallback triggered for file={}", fileName, ex);
        // DO NOT fail payment because receipt upload failed (CRITICAL BUSINESS RULE)
        return null;
    }

    @CircuitBreaker(name = "fileService", fallbackMethod = "fallbackDelete")
    public void deleteFile(String fileUrl) {
        try {
            webClient.delete()
                    .uri(uriBuilder ->
                            uriBuilder.path("/api/files/delete")
                                    .queryParam("fileUrl", fileUrl)
                                    .build()
                    )
                    .retrieve()
                    .toBodilessEntity()
                    .timeout(Duration.ofSeconds(5))
                    .block();

            log.info("File deleted: {}", fileUrl);

        } catch (Exception ex) {
            log.error("Failed to delete file {}", fileUrl, ex);
        }
    }

    public void fallbackDelete(String fileUrl, Throwable ex) {
        log.error("File delete fallback triggered for {}", fileUrl, ex);
    }
}