package com.arka.iam_service.identity.domain.event.impl;

import com.arka.iam_service.identity.domain.event.IdentityEvent;
import com.arka.iam_service.identity.domain.model.vo.UserId;

import java.time.Instant;

public record UserUnlocked(
        UserId userId,
        Instant occurredAt
) implements IdentityEvent {}