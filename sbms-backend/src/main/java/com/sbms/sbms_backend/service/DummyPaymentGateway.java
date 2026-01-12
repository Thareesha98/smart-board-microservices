package com.sbms.sbms_backend.service;


import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DummyPaymentGateway {

    public boolean simulateGatewayResponse() {
        // 90% success rate (realistic)
        return Math.random() > 0.1;
    }

    public String generateTransactionRef() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 10);
    }
}
