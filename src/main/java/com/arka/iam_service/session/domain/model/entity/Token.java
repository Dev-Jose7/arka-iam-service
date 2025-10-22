package com.arka.iam_service.session.domain.model.entity;


import com.arka.iam_service.session.domain.model.enums.TokenType;
import com.arka.iam_service.session.domain.model.vo.JwtId;

import java.time.LocalDateTime;
import java.util.Objects;

public class Token {

    private final JwtId JwtId;
    private final TokenType type;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiresAt;
    private LocalDateTime revokedAt;
    private boolean revoked;

    public Token(JwtId JwtId, TokenType type, LocalDateTime issuedAt, LocalDateTime expiresAt) {
        this.JwtId = Objects.requireNonNull(JwtId, "JwtId cannot be null.");
        this.type = Objects.requireNonNull(type, "Type cannot be null.");
        this.issuedAt = Objects.requireNonNull(issuedAt, "issuedAt cannot be null.");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt cannot be null.");

        if (expiresAt.isBefore(issuedAt)) {
            throw new IllegalArgumentException("Expiration time must be after issue time.");
        }

        this.revokedAt = null;
        this.revoked = false;
    }

    public void revoke() {
        if (revoked) {
            throw new IllegalStateException("Token is already revoked.");
        }

        this.revoked = true;
        this.revokedAt = LocalDateTime.now();
    }

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(expiresAt);
    }

    public boolean isRevoked() {
        return revoked;
    }

    public JwtId getJwtId() {
        return JwtId;
    }

    public TokenType getType() {
        return type;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(JwtId, token.JwtId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(JwtId);
    }

    @Override
    public String toString() {
        return "Token{" +
                "JwtId=" + JwtId +
                ", type=" + type +
                ", issuedAt=" + issuedAt +
                ", expiresAt=" + expiresAt +
                ", revoked=" + revoked +
                '}';
    }
}

