package com.arka.iam_service.identity.domain.model.enums;

public enum CredentialType {
    USERNAME_PASSWORD,      // email/password (local)
    OAUTH_TOKEN,   // Google, GitHub, etc.
    API_KEY        // Service
}
