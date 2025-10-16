package com.arka.iam_service.identity.domain.model.entity;

import com.arka.iam_service.identity.domain.model.enums.AuthProvider;
import com.arka.iam_service.identity.domain.model.enums.CredentialType;
import com.arka.iam_service.identity.domain.model.vo.CredentialId;
import com.arka.iam_service.identity.domain.model.vo.Email;
import com.arka.iam_service.identity.domain.model.vo.HashedPassword;

import java.time.Instant;
import java.util.Objects;

public final class Credential {

    private final CredentialId credentialId;
    private final CredentialType credentialType;
    private final AuthProvider authProvider;

    private Email email;                // for USERNAME_PASSWORD y OAUTH_TOKEN
    private HashedPassword password;    // for USERNAME_PASSWORD only
    private final String oauthToken;          // for OAUTH_TOKEN only
    private final String externalUserId;      // for OAUTH_TOKEN only (ej: sub de JWT)
    private final Instant tokenExpiresAt;     // for OAUTH_TOKEN only
    private final String apiKey;              // for API_KEY only

    private Credential(
            CredentialId credentialId,
            CredentialType credentialType,
            AuthProvider authProvider,
            Email email,
            HashedPassword password,
            String oauthToken,
            String externalUserId,
            Instant tokenExpiresAt,
            String apiKey
    ) {
        this.credentialId = Objects.requireNonNull(credentialId);
        this.credentialType = Objects.requireNonNull(credentialType);
        this.authProvider = Objects.requireNonNull(authProvider);
        this.email = email;
        this.password = password;
        this.oauthToken = oauthToken;
        this.externalUserId = externalUserId;
        this.tokenExpiresAt = tokenExpiresAt;
        this.apiKey = apiKey;
    }

    public static Credential forUsernamePassword(
            CredentialId id,
            AuthProvider authProvider,
            Email email,
            HashedPassword password
    ) {
        if (authProvider != AuthProvider.LOCAL) {
            throw new IllegalArgumentException("USERNAME_PASSWORD is only supported with LOCAL auth provider.");
        }
        return new Credential(id, CredentialType.USERNAME_PASSWORD, authProvider, email, password,
                null, null, null, null);
    }

    public static Credential forOAuthToken(
            CredentialId id,
            AuthProvider authProvider,
            Email email,
            String oauthToken,
            String externalUserId,
            Instant tokenExpiresAt
    ) {
        if (authProvider == AuthProvider.LOCAL) {
            throw new IllegalArgumentException("OAUTH_TOKEN cannot be used with LOCAL auth provider.");
        }
        return new Credential(id, CredentialType.OAUTH_TOKEN, authProvider, email,
                null, oauthToken, externalUserId, tokenExpiresAt, null);
    }

    public static Credential forApiKey(
            CredentialId id,
            String apiKey
    ) {
        return new Credential(id, CredentialType.API_KEY, AuthProvider.LOCAL,
                null, null, null, null, null, apiKey);
    }

    public void changeEmail(Email newEmail) {
        if (credentialType != CredentialType.USERNAME_PASSWORD && credentialType != CredentialType.OAUTH_TOKEN) {
            throw new IllegalStateException("Email is not supported for credential type: " + credentialType);
        }

        this.email = newEmail;
    }

    public void changePassword(HashedPassword newPassword) {
        if (credentialType != CredentialType.USERNAME_PASSWORD) {
            throw new IllegalStateException("Password is only supported for USERNAME_PASSWORD credentials.");
        }

        this.password = newPassword;
    }

    public CredentialId getCredentialId() {
        return credentialId;
    }

    public CredentialType getCredentialType() {
        return credentialType;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public Email getEmail() {
        return email;
    }

    public HashedPassword getPassword() {
        return password;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public String getExternalUserId() {
        return externalUserId;
    }

    public Instant getTokenExpiresAt() {
        return tokenExpiresAt;
    }

    public String getApiKey() {
        return apiKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Credential that)) return false;
        return credentialId.equals(that.credentialId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(credentialId);
    }

    @Override
    public String toString() {
        return "Credential{" +
                "credentialId=" + credentialId +
                ", credentialType=" + credentialType +
                ", authProvider=" + authProvider +
                ", email=" + email +
                ", externalUserId=" + externalUserId +
                ", tokenExpiresAt=" + tokenExpiresAt +
                '}';
    }
}
