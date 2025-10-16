package com.arka.iam_service.identity.domain.event.impl;

import com.arka.iam_service.identity.domain.event.IdentityEvent;
import com.arka.iam_service.identity.domain.model.vo.Email;
import com.arka.iam_service.identity.domain.model.vo.UserId;

import java.time.Instant;

public record UserRegistered(
        UserId userId,
        Email email,
        Instant occurredAt
) implements IdentityEvent { }
