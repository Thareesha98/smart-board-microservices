package com.sbms.sbms_payment_service.client;

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

    /**
     * Use dedicated WebClient built from Builder (NO Qualifier, NO bean conflicts)
     * File service = sbms-backend (as per your architecture)
     */
    public FileClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://sbms-backend:8080") // Kubernetes service DNS
                .build();
    }

    /**
     * Upload PDF bytes to File Service
     * Endpoint: POST /api/files/upload/{folder}
     * Form field: file (MultipartFile)
     *
     * CRITICAL RULE:
     * Payment MUST NOT fail if file upload fails.
     */
    @CircuitBreaker(name = "fileService", fallbackMethod = "fallbackUpload")
    @Retry(name = "fileService")
    public String uploadBytes(byte[] bytes, String fileName, String folder) {

        if (bytes == null || bytes.length == 0) {
            log.warn("Skipping file upload: empty bytes for file={}", fileName);
            return null;
        }

        final String targetFolder =
                (folder == null || folder.isBlank()) ? "receipts" : folder;

        final String safeFileName =
                (fileName == null || fileName.isBlank()) ? "receipt.pdf" : fileName;

        try {
            log.info("Uploading file to File Service. fileName={}, folder={}", safeFileName, targetFolder);

            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

            bodyBuilder.part(
                    "file",
                    new ByteArrayResource(bytes) {
                        @Override
                        public String getFilename() {
                            return safeFileName;
                        }
                    }
            ).contentType(MediaType.APPLICATION_PDF);

            String fileUrl = webClient.post()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path("/api/files/upload/{folder}")
                                    .build(targetFolder) // ✅ now final → no compile error
                    )
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(bodyBuilder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            log.info("File uploaded successfully. URL={}", fileUrl);
            return fileUrl;

        } catch (Exception ex) {
            // Non-blocking (critical for payment flow)
            log.error("File upload FAILED for fileName={} (non-blocking)", safeFileName, ex);
            return null;
        }
    }

    /**
     * Fallback when File Service is down
     * Business Rule: NEVER break payment flow
     */
    public String fallbackUpload(byte[] bytes, String fileName, String folder, Throwable ex) {
        log.error(
                "File Service DOWN. Fallback triggered. fileName={}, folder={}",
                fileName,
                folder,
                ex
        );
        return null; // graceful degradation (correct for payment systems)
    }

    /**
     * Delete file from File Service
     * Endpoint: DELETE /api/files/delete?fileUrl=...
     */
    @CircuitBreaker(name = "fileService", fallbackMethod = "fallbackDelete")
    public void deleteFile(String fileUrl) {

        if (fileUrl == null || fileUrl.isBlank()) {
            log.warn("Skipping file delete: fileUrl is null/empty");
            return;
        }

        try {
            log.info("Deleting file from File Service. fileUrl={}", fileUrl);

            webClient.delete()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path("/api/files/delete")
                                    .queryParam("fileUrl", fileUrl)
                                    .build()
                    )
                    .retrieve()
                    .toBodilessEntity()
                    .timeout(Duration.ofSeconds(5))
                    .block();

            log.info("File deleted successfully: {}", fileUrl);

        } catch (Exception ex) {
            // Non-critical failure
            log.error("Failed to delete file (non-blocking) fileUrl={}", fileUrl, ex);
        }
    }

    public void fallbackDelete(String fileUrl, Throwable ex) {
        log.error(
                "File delete fallback triggered. fileUrl={}",
                fileUrl,
                ex
        );
    }
}