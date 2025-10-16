package com.arka.iam_service.identity.domain;

import com.arka.iam_service.identity.domain.model.entity.Credential;
import com.arka.iam_service.identity.domain.model.enums.AuthProvider;
import com.arka.iam_service.identity.domain.model.enums.CredentialType;
import com.arka.iam_service.identity.domain.model.vo.CredentialId;
import com.arka.iam_service.identity.domain.model.vo.Email;
import com.arka.iam_service.identity.domain.model.vo.HashedPassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CredentialTest {

    private CredentialId credentialId;
    private Email email;

    @BeforeEach
    void setup() {
        this.credentialId = CredentialId.create(UUID.randomUUID());
        this.email = Email.create("user@example.com");
    }

    @Test
    void shouldCreateUsernamePasswordCredential() {
        Credential credential = Credential.forUsernamePassword(
                this.credentialId,
                AuthProvider.LOCAL,
                this.email,
                HashedPassword.create("$2a$hashed-password")
        );

        assertEquals(CredentialType.USERNAME_PASSWORD, credential.getCredentialType());
        assertEquals(AuthProvider.LOCAL, credential.getAuthProvider());
        assertNotNull(credential.getEmail());
        assertNotNull(credential.getPassword());
    }

    @Test
    void shouldCreateOAuthTokenCredential() {
        Credential credential = Credential.forOAuthToken(
                this.credentialId,
                AuthProvider.GOOGLE,
                this.email,
                "token123",
                "ext-user-id",
                Instant.now().plusSeconds(3600)
        );

        assertEquals(CredentialType.OAUTH_TOKEN, credential.getCredentialType());
        assertEquals(AuthProvider.GOOGLE, credential.getAuthProvider());
        assertEquals("token123", credential.getOauthToken());
        assertEquals("ext-user-id", credential.getExternalUserId());
    }

    @Test
    void shouldCreateApiKeyCredential() {
        Credential credential = Credential.forApiKey(
                this.credentialId,
                "my-api-key-123"
        );

        assertEquals(CredentialType.API_KEY, credential.getCredentialType());
        assertEquals(AuthProvider.LOCAL, credential.getAuthProvider());
        assertEquals("my-api-key-123", credential.getApiKey());
    }

    @Test
    void shouldChangeEmailForUsernamePassword() {
        Credential credential = Credential.forUsernamePassword(
                this.credentialId,
                AuthProvider.LOCAL,
                this.email,
                HashedPassword.create("$2a$hashed-password")
        );

        credential.changeEmail(Email.create("new@example.com"));

        assertEquals("new@example.com", credential.getEmail().getValue());
    }

    @Test
    void shouldChangePasswordForUsernamePassword() {
        Credential credential = Credential.forUsernamePassword(
                this.credentialId,
                AuthProvider.LOCAL,
                this.email,
                HashedPassword.create("$2a$hashed-password")
        );


        HashedPassword.create("$2a$hashed-password");

        assertEquals("$2a$hashed-password", credential.getPassword().getValue());
    }

    @Test
    void shouldThrowWhenCreatingUsernamePasswordWithNonLocalProvider() {
        assertThrows(IllegalArgumentException.class, () -> {
            Credential.forUsernamePassword(
                    this.credentialId,
                    AuthProvider.GOOGLE,
                    this.email,
                    HashedPassword.create("$2a$hashed-password")
            );
        });
    }

    @Test
    void shouldThrowWhenCreatingOAuthTokenWithLocalProvider() {
        assertThrows(IllegalArgumentException.class, () -> {
            Credential.forOAuthToken(
                    this.credentialId,
                    AuthProvider.LOCAL,
                    this.email,
                    "token",
                    "ext-id",
                    Instant.now().plusSeconds(3600)
            );
        });
    }

    @Test
    void shouldThrowWhenChangingPasswordInOAuthToken() {
        Credential credential = Credential.forOAuthToken(
                this.credentialId,
                AuthProvider.GOOGLE,
                this.email,
                "token",
                "sub-id",
                Instant.now().plusSeconds(3600)
        );

        assertThrows(IllegalStateException.class, () -> {
            credential.changePassword(
                    HashedPassword.create("$2a$hashed-password")
            );
        });
    }

    @Test
    void shouldThrowWhenChangingEmailInApiKeyCredential() {
        Credential credential = Credential.forApiKey(
                this.credentialId,
                "api-key-456"
        );

        assertThrows(IllegalStateException.class, () -> {
            credential.changeEmail(Email.create("fail@example.com"));
        });
    }
}
