package com.arka.iam_service.identity.domain.event.impl;

import com.arka.iam_service.identity.domain.event.IdentityEvent;
import com.arka.iam_service.identity.domain.model.vo.UserId;

import java.time.Instant;
import java.time.LocalDateTime;

public record UserEmailVerified(
        UserId userId,
        Instant verifiedAt,
        Instant occurredAt
) implements IdentityEvent { }
