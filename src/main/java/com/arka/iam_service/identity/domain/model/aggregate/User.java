package com.arka.iam_service.identity.domain.model.aggregate;

import com.arka.iam_service.identity.domain.model.entity.Credential;
import com.arka.iam_service.identity.domain.model.enums.AuthProvider;
import com.arka.iam_service.identity.domain.model.enums.CredentialType;
import com.arka.iam_service.identity.domain.model.enums.UserStatus;
import com.arka.iam_service.identity.domain.event.IdentityEvent;
import com.arka.iam_service.identity.domain.event.impl.*;
import com.arka.iam_service.identity.domain.model.vo.Email;
import com.arka.iam_service.identity.domain.model.vo.EmailVerification;
import com.arka.iam_service.identity.domain.model.vo.HashedPassword;
import com.arka.iam_service.identity.domain.model.vo.UserId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class User {

    private final UserId userId;
    private UserStatus status;
    private EmailVerification emailVerification;

    private final List<Credential> credentials = new ArrayList<>();
    private final List<IdentityEvent> identityEvents = new ArrayList<>();

    private User(UserId userId, EmailVerification emailVerification) {
        this.userId = Objects.requireNonNull(userId);
        this.status = UserStatus.ENABLED;
        this.emailVerification = Objects.requireNonNull(emailVerification);
    }

    public static User register(UserId userId, Email email,
                                EmailVerification emailVerification, Instant now) {
        User user = new User(userId, emailVerification);
        user.identityEvents.add(new UserRegistered(userId, email, now));
        return user;
    }

    public void changeEmail(Email newEmail, Instant now) {
        Credential usernamePassword = getCredential(CredentialType.USERNAME_PASSWORD, AuthProvider.LOCAL);
        Email oldEmail = usernamePassword.getEmail();

        usernamePassword.changeEmail(newEmail);

        identityEvents.add(new UserEmailChanged(
                this.userId,
                oldEmail,
                newEmail,
                now
        ));
    }

    public void changePassword(HashedPassword password, Instant now) {
        Credential usernamePassword = getCredential(CredentialType.USERNAME_PASSWORD, AuthProvider.LOCAL);

        usernamePassword.changePassword(password);

        identityEvents.add(new UserPasswordChanged(
                this.userId,
                usernamePassword.getCredentialId(),
                now));
    }

    public void lock(String reason, Instant now) {
        this.status = UserStatus.LOCKED;
        this.identityEvents.add(new UserLocked(this.userId, reason, now));
    }

    public void unlock(Instant now) {
        this.status = UserStatus.ENABLED;
        this.identityEvents.add(new UserUnlocked(this.userId, now));
    }

    public void verifyEmail(String code, Instant now) {
        this.emailVerification = this.emailVerification.verify(code, now);
        this.identityEvents.add(new UserEmailVerified(this.userId, emailVerification.getVerifiedAt(), Instant.now()));
    }

    public void addCredential(Credential credential, Instant now) {
        boolean alreadyExists = this.credentials.stream()
                .anyMatch(c ->
                        c.getCredentialType() == credential.getCredentialType() &&
                        c.getAuthProvider() == credential.getAuthProvider());


        if (alreadyExists) {
            throw new IllegalStateException(
                    "Credential of type " + credential.getCredentialType() +
                            " with provider " + credential.getAuthProvider() +
                            " already exists for this user.");
        }

        this.credentials.add(credential);
        this.identityEvents.add(new CredentialAdded(
                this.userId,
                credential.getCredentialId(),
                credential.getCredentialType(),
                credential.getAuthProvider(),
                now
        ));
    }

    public List<IdentityEvent> pullDomainEvents() {
        List<IdentityEvent> events = List.copyOf(identityEvents);
        identityEvents.clear();
        return events;
    }

    private Credential getCredential(CredentialType type, AuthProvider provider) {
        return this.credentials.stream()
                .filter(credential ->
                        credential.getCredentialType() == type &&
                                credential.getAuthProvider() == provider)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Credential not found for type " + type + " and provider " + provider));
    }

    public UserId getUserId() {
        return userId;
    }

    public UserStatus getStatus() {
        return status;
    }

    public EmailVerification getEmailVerification() {
        return emailVerification;
    }

    public List<Credential> getCredentials() {
        return Collections.unmodifiableList(this.credentials);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", status=" + status +
                ", emailVerification=" + emailVerification +
                ", credentials=" + credentials +
                '}';
    }
}
