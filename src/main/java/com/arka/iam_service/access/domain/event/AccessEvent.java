package com.arka.iam_service.access.domain.event;

import java.time.Instant;

public interface AccessEvent {
    Instant occurredAt();
}
