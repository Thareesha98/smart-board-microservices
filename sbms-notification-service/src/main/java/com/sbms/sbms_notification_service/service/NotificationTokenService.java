package com.sbms.sbms_notification_service.service;

import com.sbms.sbms_notification_service.model.NotificationToken;
import com.sbms.sbms_notification_service.repository.NotificationTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationTokenService {

    private final NotificationTokenRepository repo;

    public NotificationTokenService(NotificationTokenRepository repo) {
        this.repo = repo;
    }

    public void save(String userId, String expoToken) {
        NotificationToken token = repo.findByUserId(userId)
                .orElse(new NotificationToken());

        token.setUserId(userId);
        token.setExpoToken(expoToken);

        repo.save(token);
    }

    public String getTokenForUser(String userId) {
        return repo.findByUserId(userId)
                .map(NotificationToken::getExpoToken)
                .orElse(null);
    }
}
