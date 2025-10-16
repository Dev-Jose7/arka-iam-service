package com.arka.iam_service.identity.domain.event.impl;

import com.arka.iam_service.identity.domain.model.enums.AuthProvider;
import com.arka.iam_service.identity.domain.model.enums.CredentialType;
import com.arka.iam_service.identity.domain.event.IdentityEvent;
import com.arka.iam_service.identity.domain.model.vo.CredentialId;
import com.arka.iam_service.identity.domain.model.vo.UserId;

import java.time.Instant;

public record CredentialAdded(
        UserId userId,
        CredentialId credentialId,
        CredentialType credentialType,
        AuthProvider authProvider,
        Instant occurredAt
) implements IdentityEvent {}

