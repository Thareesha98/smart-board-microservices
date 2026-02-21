package com.sbms.sbms_payment_service.client;


import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Service
@Slf4j
public class FileClient {

    private final WebClient webClient;

    public FileClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://sbms-backend:8080")
                .build();
    }

    @CircuitBreaker(name = "fileService", fallbackMethod = "fallbackUpload")
    @Retry(name = "fileService")
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
                .block();
    }

    public String fallbackUpload(byte[] bytes, String fileName, String folder, Throwable ex) {
        log.error("File upload failed for receipt {}. Returning null URL.", fileName, ex);
        return null; // graceful fallback (payment still succeeds)
    }
}