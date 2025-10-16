package com.arka.iam_service.identity.domain;

import com.arka.iam_service.identity.domain.model.enums.EmailVerificationStatus;
import com.arka.iam_service.identity.domain.model.vo.EmailVerification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class EmailVerificationTest {

    private String VALID_CODE;
    private Instant NOW;
    private Instant EXPIRES_AT;

    private EmailVerification unverified;

    @BeforeEach
    public void setUp() {
        VALID_CODE = "ABC123";
        NOW = Instant.now();
        EXPIRES_AT = NOW.plus(10, ChronoUnit.MINUTES);

        unverified = EmailVerification.unverified(VALID_CODE, EXPIRES_AT);
    }

    @Test
    public void shouldVerifySuccessfullyWithCorrectCodeBeforeExpiry() {
        Instant verifyAt = NOW.plus(5, ChronoUnit.MINUTES); // Simulate real action

        EmailVerification verified = unverified.verify(VALID_CODE, verifyAt);

        assertTrue(verified.isVerified());
        assertEquals(EmailVerificationStatus.VERIFIED, verified.getStatus());
        assertEquals(verifyAt, verified.getVerifiedAt());
        assertEquals(VALID_CODE, verified.getVerificationCode());
        assertEquals(EXPIRES_AT, verified.getCodeExpiresAt());
    }

    @Test
    public void shouldThrowWhenCodeIsIncorrect() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                unverified.verify("WRONGCODE", NOW.plus(2, ChronoUnit.MINUTES))
        );

        assertEquals("Invalid verification code.", ex.getMessage());
    }

    @Test
    public void shouldThrowWhenCodeIsExpired() {
        Instant afterExpiry = EXPIRES_AT.plusSeconds(1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                unverified.verify(VALID_CODE, afterExpiry)
        );

        assertEquals("Verification code has expired.", ex.getMessage());
    }

    @Test
    public void shouldThrowWhenAlreadyVerified() {
        Instant verifiedAt = NOW.plus(1, ChronoUnit.MINUTES);
        EmailVerification verified = unverified.verify(VALID_CODE, verifiedAt);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                verified.verify(VALID_CODE, verifiedAt.plus(1, ChronoUnit.MINUTES))
        );

        assertEquals("Email already verified.", ex.getMessage());
    }
}

