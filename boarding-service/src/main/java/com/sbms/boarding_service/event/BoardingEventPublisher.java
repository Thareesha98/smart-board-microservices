package com.sbms.boarding_service.event;

import java.util.Map;

public interface BoardingEventPublisher {

    void publish(
            String eventType,
            Long targetUserId,
            Long boardingId,
            Map<String, Object> data
    );
}
