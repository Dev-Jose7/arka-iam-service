package com.arka.iam_service.identity.domain.event.impl;

import com.arka.iam_service.identity.domain.event.IdentityEvent;
import com.arka.iam_service.identity.domain.model.vo.Email;
import com.arka.iam_service.identity.domain.model.vo.UserId;

import java.time.Instant;

public record UserEmailChanged(
        UserId userId,
        Email oldEmail,
        Email newEmail,
        Instant occurredAt
) implements IdentityEvent { }
