package com.arka.iam_service.identity.domain.model.vo;

import com.arka.iam_service.identity.domain.model.enums.EmailVerificationStatus;

import java.time.Instant;
import java.util.Objects;

public final class EmailVerification {

    private String verificationCode;
    private Instant codeExpiresAt;
    private Instant verifiedAt;
    private EmailVerificationStatus status;

    public EmailVerification(
            String verificationCode,
            Instant codeExpiresAt,
            Instant verifiedAt,
            EmailVerificationStatus status
    ) {
        this.verificationCode = verificationCode;
        this.codeExpiresAt = codeExpiresAt;
        this.verifiedAt = verifiedAt;
        this.status = status;
    }

    public static EmailVerification unverified(String code, Instant expiresAt) {
        return new EmailVerification(code, expiresAt, null, EmailVerificationStatus.UNVERIFIED);
    }

    public EmailVerification verify(String code, Instant now) {
        if (this.status == EmailVerificationStatus.VERIFIED) {
            throw new IllegalArgumentException("Email already verified.");
        }

        if (!this.verificationCode.equals(code)) {
            throw new IllegalArgumentException("Invalid verification code.");
        }

        if (now.isAfter(this.codeExpiresAt)) {
            throw new IllegalArgumentException("Verification code has expired.");
        }

        return new EmailVerification(
                this.verificationCode,
                this.codeExpiresAt,
                now,
                EmailVerificationStatus.VERIFIED
        );
    }

    public boolean isVerified() {
        return this.status == EmailVerificationStatus.VERIFIED;
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(this.codeExpiresAt);
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public Instant getCodeExpiresAt() {
        return codeExpiresAt;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    public EmailVerificationStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailVerification that = (EmailVerification) o;
        return Objects.equals(verificationCode, that.verificationCode) &&
                Objects.equals(codeExpiresAt, that.codeExpiresAt) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(verificationCode, codeExpiresAt, status);
    }
}
