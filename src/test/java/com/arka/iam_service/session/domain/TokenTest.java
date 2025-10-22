package com.arka.iam_service.session.domain;

import com.arka.iam_service.session.domain.model.entity.Token;
import com.arka.iam_service.session.domain.model.enums.TokenType;
import com.arka.iam_service.session.domain.model.vo.JwtId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class TokenTest {

    private JwtId jwtId;
    private Token accessToken;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;

    @BeforeEach
    void setUp() {
        jwtId = JwtId.generate();
        issuedAt = LocalDateTime.now();
        expiresAt = issuedAt.plusHours(1);
        accessToken = new Token(jwtId, TokenType.ACCESS, issuedAt, expiresAt);
    }

    @Test
    void shouldCreateTokenSuccessfully() {
        Assertions.assertNotNull(accessToken);
        Assertions.assertEquals(jwtId, accessToken.getJwtId());
        Assertions.assertEquals(TokenType.ACCESS, accessToken.getType());
        Assertions.assertEquals(issuedAt, accessToken.getIssuedAt());
        Assertions.assertEquals(expiresAt, accessToken.getExpiresAt());
        Assertions.assertFalse(accessToken.isRevoked());
    }

    @Test
    void shouldRevokeTokenSuccessfully() {
        accessToken.revoke();
        Assertions.assertTrue(accessToken.isRevoked());
    }

    @Test
    void shouldReturnTrueWhenTokenIsExpired() {
        LocalDateTime afterExpiration = expiresAt.plusMinutes(1);
        Assertions.assertTrue(accessToken.isExpired(afterExpiration));
    }

    @Test
    void shouldReturnFalseWhenTokenIsNotExpired() {
        LocalDateTime beforeExpiration = expiresAt.minusMinutes(1);
        Assertions.assertFalse(accessToken.isExpired(beforeExpiration));
    }

    @Test
    void shouldRespectEqualityBasedOnJwtId() {
        Token anotherToken = new Token(jwtId, TokenType.ACCESS, issuedAt, expiresAt);
        Assertions.assertEquals(accessToken, anotherToken);
        Assertions.assertEquals(accessToken.hashCode(), anotherToken.hashCode());
    }

    @Test
    void shouldThrowWhenRevokeAlreadyRevokedToken() {
        accessToken.revoke();
        Exception exception = Assertions.assertThrows(IllegalStateException.class, accessToken::revoke);
        Assertions.assertEquals("Token is already revoked.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenJwtIdIsNull() {
        Exception exception = Assertions.assertThrows(NullPointerException.class, () -> {
            new Token(null, TokenType.ACCESS, issuedAt, expiresAt);
        });
        Assertions.assertTrue(exception.getMessage().contains("JwtId cannot be null"));
    }

    @Test
    void shouldThrowWhenTypeIsNull() {
        Exception exception = Assertions.assertThrows(NullPointerException.class, () -> {
            new Token(jwtId, null, issuedAt, expiresAt);
        });
        Assertions.assertTrue(exception.getMessage().contains("Type cannot be null"));
    }

    @Test
    void shouldThrowWhenIssuedAtIsNull() {
        Exception exception = Assertions.assertThrows(NullPointerException.class, () -> {
            new Token(jwtId, TokenType.ACCESS, null, expiresAt);
        });
        Assertions.assertTrue(exception.getMessage().contains("issuedAt cannot be null"));
    }

    @Test
    void shouldThrowWhenExpiresAtIsNull() {
        Exception exception = Assertions.assertThrows(NullPointerException.class, () -> {
            new Token(jwtId, TokenType.ACCESS, issuedAt, null);
        });
        Assertions.assertTrue(exception.getMessage().contains("expiresAt cannot be null"));
    }

    @Test
    void shouldThrowWhenExpiresBeforeIssuedAt() {
        LocalDateTime invalidExpires = issuedAt.minusHours(1);
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Token(jwtId, TokenType.ACCESS, issuedAt, invalidExpires);
        });
        Assertions.assertEquals("Expiration time must be after issue time.", exception.getMessage());
    }
}
