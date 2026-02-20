package com.sbms.sbms_payment_service.client;


import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class FileClient {

    private final WebClient webClient;

    // Calls sbms-backend service inside Kubernetes
    public FileClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://sbms-backend:8080")
                .build();
    }

    /**
     * Upload raw bytes as file (for PDF receipts)
     */
    public String uploadBytes(byte[] bytes, String fileName, String folder) {

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

        return webClient.post()
                .uri("/api/files/upload/{folder}", folder)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(bodyBuilder.build())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(ex -> {
                    throw new RuntimeException("File upload failed: " + ex.getMessage());
                })
                .block();
    }

    /**
     * Delete file from S3 via sbms-backend
     */
    public void deleteFile(String fileUrl) {
        webClient.delete()
                .uri(uriBuilder ->
                        uriBuilder.path("/api/files/delete")
                                .queryParam("fileUrl", fileUrl)
                                .build()
                )
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
