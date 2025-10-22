package com.arka.iam_service.session.domain.event;

import java.time.Instant;

public interface SessionEvent {
    Instant occurredAt();
}
