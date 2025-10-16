package com.arka.iam_service.identity.domain;

import com.arka.iam_service.identity.domain.model.aggregate.User;
import com.arka.iam_service.identity.domain.model.entity.Credential;
import com.arka.iam_service.identity.domain.model.enums.AuthProvider;
import com.arka.iam_service.identity.domain.model.enums.UserStatus;
import com.arka.iam_service.identity.domain.event.IdentityEvent;
import com.arka.iam_service.identity.domain.event.impl.*;
import com.arka.iam_service.identity.domain.model.vo.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class UserTest {

    private UserId userId;
    private Email email;
    private EmailVerification verification;
    private Instant now;
    private Credential credential;

    @BeforeEach
    public void setup() {
        userId = UserId.create(UUID.randomUUID());
        email = Email.create("user@example.com");
        now = Instant.now();
        verification = EmailVerification.unverified("123456", now.plus(10, ChronoUnit.MINUTES));
        credential = Credential.forUsernamePassword(
                CredentialId.create(UUID.randomUUID()),
                AuthProvider.LOCAL,
                email,
                HashedPassword.create("$2a$hashed-password")
        );
    }

    @Test
    void shouldRegisterUser() {
        User user = User.register(userId, email, verification, now);

        List<IdentityEvent> events = user.pullDomainEvents();

        Assertions.assertTrue(events.get(0) instanceof UserRegistered);
        Assertions.assertEquals(1, events.size());
        Assertions.assertFalse(events.isEmpty());
    }

    @Test
    void shouldAddCredentialSuccessfully() {
        User user = User.register(userId, email, verification, now);
        user.addCredential(credential, now);

        List<IdentityEvent> events = user.pullDomainEvents();

        Assertions.assertEquals(2, events.size()); // includes UserRegistered + CredentialAdded
        Assertions.assertTrue(events.get(1) instanceof CredentialAdded);
    }

    @Test
    void shouldChangeEmailSuccessfully() {
        User user = User.register(userId, email, verification, now);
        user.addCredential(credential, now);

        Email newEmail = Email.create("new@example.com");
        user.changeEmail(newEmail, now);

        List<IdentityEvent> events = user.pullDomainEvents();
        Assertions.assertEquals(3, events.size()); // includes UserRegistered + CredentialAdded + UserEmailChanged
        Assertions.assertTrue(events.get(2) instanceof UserEmailChanged);
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        User user = User.register(userId, email, verification, now);
        user.addCredential(credential, now);

        HashedPassword newPassword = HashedPassword.create("$2a$new-password");
        user.changePassword(newPassword, now);

        List<IdentityEvent> events = user.pullDomainEvents();
        Assertions.assertEquals(3, events.size());
        Assertions.assertTrue(events.get(2) instanceof UserPasswordChanged);
    }

    @Test
    void shouldLockAndUnlockUser() {
        User user = User.register(userId, email, verification, now);
        user.lock("Too many failed attempts", now);

        Assertions.assertEquals(UserStatus.LOCKED, user.getStatus());

        user.unlock(now);
        Assertions.assertEquals(UserStatus.ENABLED, user.getStatus());
    }

    @Test
    void shouldVerifyEmailSuccessfully() {
        User user = User.register(userId, email, verification, now);

        user.verifyEmail("123456", now);

        List<IdentityEvent> events = user.pullDomainEvents();
        Assertions.assertEquals(2, events.size()); // includes UserRegistered + UserEmailVerified
        Assertions.assertTrue(events.get(1) instanceof UserEmailVerified);
    }

    @Test
    void shouldThrowWhenAddingDuplicateCredential() {
        User user = User.register(userId, email, verification, now);
        user.addCredential(credential, now);

        Credential duplicate = Credential.forUsernamePassword(
                CredentialId.create(UUID.randomUUID()),
                AuthProvider.LOCAL,
                email,
                HashedPassword.create("$2a$another-password")
        );

        Assertions.assertThrows(IllegalStateException.class, () -> {
            user.addCredential(duplicate, now);
        });
    }

    @Test
    void shouldThrowWhenChangingEmailWithoutCredential() {
        User user = User.register(userId, email, verification, now);
        Email newEmail = Email.create("new@example.com");

        Assertions.assertThrows(IllegalStateException.class, () -> {
            user.changeEmail(newEmail, now);
        });
    }

    @Test
    void shouldThrowWhenChangingPasswordWithoutCredential() {
        User user = User.register(userId, email, verification, now);
        HashedPassword hashedPassword = HashedPassword.create("$2a$new-password");

        Assertions.assertThrows(IllegalStateException.class, () -> {
            user.changePassword(hashedPassword, now);
        });
    }

    @Test
    void shouldThrowWhenVerificationCodeIsInvalid() {
        User user = User.register(userId, email, verification, now);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            user.verifyEmail("wrong-code", now);
        });
    }
}
