package com.arka.iam_service.session.domain;

import com.arka.iam_service.session.domain.event.SessionEvent;
import com.arka.iam_service.session.domain.model.aggregate.Session;
import com.arka.iam_service.session.domain.model.entity.Token;
import com.arka.iam_service.session.domain.model.enums.TokenType;
import com.arka.iam_service.session.domain.model.vo.JwtId;
import com.arka.iam_service.session.domain.model.vo.SessionId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

class SessionDomainIntegrationTest {

    private Session session;
    private SessionId sessionId;
    private UUID userId;
    private Token accessToken;
    private Token refreshToken;

    @BeforeEach
    void setUp() {
        sessionId = SessionId.generate();
        userId = UUID.randomUUID();

        accessToken = new Token(
                JwtId.generate(),
                TokenType.ACCESS,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15)
        );

        refreshToken = new Token(
                JwtId.generate(),
                TokenType.REFRESH,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)
        );

        session = new Session(sessionId, userId);
        session.pullDomainEvents();
    }

    @Test
    void shouldCreateSessionAndAddTokens() {
        session.addToken(accessToken);
        session.addToken(refreshToken);

        List<SessionEvent> events = session.pullDomainEvents();

        Assertions.assertEquals(2, events.size());
        Assertions.assertEquals("TokenIssued", events.get(0).getClass().getSimpleName());
        Assertions.assertTrue(session.getTokens().contains(accessToken));
        Assertions.assertTrue(session.getTokens().contains(refreshToken));
    }

    @Test
    void shouldRevokeSingleToken() {
        session.addToken(accessToken);
        session.pullDomainEvents();

        session.revokeTokenByJti(accessToken.getJwtId());
        List<SessionEvent> events = session.pullDomainEvents();

        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("TokenRevoked", events.get(0).getClass().getSimpleName());
        Assertions.assertTrue(accessToken.isRevoked());
    }

    @Test
    void shouldRotateAccessTokenSuccessfully() {
        session.addToken(accessToken);
        session.pullDomainEvents();

        Token newAccessToken = new Token(
                JwtId.generate(),
                TokenType.ACCESS,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15)
        );

        session.rotateToken(accessToken.getJwtId(), newAccessToken);
        List<SessionEvent> events = session.pullDomainEvents();

        // TokenRevoked + TokenIssued + TokenRotated
        Assertions.assertEquals(3, events.size());
        Assertions.assertEquals("TokenRotated", events.get(2).getClass().getSimpleName());
        Assertions.assertTrue(accessToken.isRevoked());
        Assertions.assertTrue(session.getTokens().contains(newAccessToken));
    }

    @Test
    void shouldRevokeSessionAndAllTokens() {
        session.addToken(accessToken);
        session.addToken(refreshToken);
        session.pullDomainEvents();

        session.revokeSession();
        List<SessionEvent> events = session.pullDomainEvents();

        Assertions.assertTrue(session.isRevoked());
        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("SessionRevoked",
                events.get(0).getClass().getSimpleName());
    }

    @Test
    void shouldExpireAccessToken() {
        LocalDateTime now = LocalDateTime.now();
        Token shortLived = new Token(
                JwtId.generate(),
                TokenType.ACCESS,
                now,
                now.plusSeconds(1)
        );

        session.addToken(shortLived);
        Assertions.assertFalse(shortLived.isExpired(now));

        LocalDateTime later = now.plusSeconds(2);
        Assertions.assertTrue(shortLived.isExpired(later));
    }

    @Test
    void shouldThrowWhenAddingDuplicateToken() {
        session.addToken(accessToken);

        Exception exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            session.addToken(accessToken);
        });

        Assertions.assertEquals("Token with same JwtId already exists in session.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenRotatingNonExistentToken() {
        Token newAccessToken = new Token(
                JwtId.generate(),
                TokenType.ACCESS,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10)
        );

        Exception exception = Assertions.assertThrows(NoSuchElementException.class, () -> {
            session.rotateToken(JwtId.generate(), newAccessToken);
        });

        Assertions.assertTrue(exception.getMessage().contains("Token not found for jti"));
    }

    @Test
    void shouldThrowWhenRevokingAlreadyRevokedSession() {
        session.revokeSession();

        Exception exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            session.revokeSession();
        });

        Assertions.assertEquals("Session already revoked.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenRevokingNonexistentToken() {
        Exception exception = Assertions.assertThrows(NoSuchElementException.class, () -> {
            session.revokeTokenByJti(JwtId.generate());
        });

        Assertions.assertTrue(exception.getMessage().contains("Token not found for jti"));
    }

    @Test
    void shouldThrowWhenRevokeAlreadyRevokedToken() {
        session.addToken(accessToken);
        accessToken.revoke();

        Exception exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            session.revokeTokenByJti(accessToken.getJwtId());
        });

        Assertions.assertEquals("Token is already revoked.", exception.getMessage());
    }
}
