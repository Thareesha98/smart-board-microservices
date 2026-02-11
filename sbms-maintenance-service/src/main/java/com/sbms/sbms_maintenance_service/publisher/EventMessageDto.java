package com.sbms.sbms_maintenance_service.publisher;

import java.util.Map;

public record EventMessageDto(
	    String eventType,
	    String userId,
	    Map<String, Object> data
	) {}
