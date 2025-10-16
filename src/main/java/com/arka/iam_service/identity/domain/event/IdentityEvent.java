package com.arka.iam_service.identity.domain.event;

import java.time.Instant;

public interface IdentityEvent {
    Instant occurredAt();
}
