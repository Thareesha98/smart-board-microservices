package com.sbms.sbms_payment_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbms.sbms_payment_service.entity.IdempotencyRecord;
import com.sbms.sbms_payment_service.repository.IdempotencyRecordRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRecordRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional
    public ResponseEntity<String> execute(
            String idempotencyKey,
            Object requestBody,
            IdempotentOperation operation
    ) {
        String requestHash = hashRequest(requestBody);

        return repository.findByIdempotencyKey(idempotencyKey)
                .map(record ->
                        ResponseEntity
                                .status(record.getResponseStatus())
                                .body(record.getResponsePayload())
                )
                .orElseGet(() -> {

                    ResponseEntity<?> response = operation.execute();

                    IdempotencyRecord record = new IdempotencyRecord();
                    record.setIdempotencyKey(idempotencyKey);
                    record.setRequestHash(requestHash);
                    record.setResponseStatus(response.getStatusCode().value());
                    record.setResponsePayload(serialize(response.getBody()));

                    repository.save(record);

                    return ResponseEntity
                            .status(response.getStatusCode())
                            .body(record.getResponsePayload());
                });
    }

    private String serialize(Object body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }

    private String hashRequest(Object body) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(objectMapper.writeValueAsBytes(body));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

    @FunctionalInterface
    public interface IdempotentOperation {
        ResponseEntity<?> execute();
    }
}

