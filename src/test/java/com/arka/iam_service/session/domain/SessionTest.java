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

class SessionTest {

    private SessionId sessionId;
    private UUID userId;
    private JwtId jwtIdAccess;
    private JwtId jwtIdRefresh;
    private Token accessToken;
    private Token refreshToken;

    @BeforeEach
    void setUp() {
        sessionId = SessionId.generate();
        userId = UUID.randomUUID();
        jwtIdAccess = JwtId.generate();
        jwtIdRefresh = JwtId.generate();

        accessToken = new Token(jwtIdAccess, TokenType.ACCESS, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        refreshToken = new Token(jwtIdRefresh, TokenType.REFRESH, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
    }

    // ✅ ---------------------- Casos positivos ----------------------

    @Test
    void shouldStartSessionAndEmitEvent() {
        Session session = new Session(sessionId, userId);

        List<SessionEvent> events = session.pullDomainEvents();
        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("SessionStarted", events.get(0).getClass().getSimpleName());
    }

    @Test
    void shouldAddTokenAndEmitEvent() {
        Session session = new Session(sessionId, userId);
        session.pullDomainEvents(); // limpiar evento inicial

        session.addToken(accessToken);
        List<SessionEvent> events = session.pullDomainEvents();

        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("TokenIssued", events.get(0).getClass().getSimpleName());
        Assertions.assertTrue(session.getTokens().contains(accessToken));
    }

    @Test
    void shouldRevokeTokenAndEmitEvent() {
        Session session = new Session(sessionId, userId);
        session.pullDomainEvents();
        session.addToken(accessToken);
        session.pullDomainEvents();

        session.revokeTokenByJti(jwtIdAccess);
        List<SessionEvent> events = session.pullDomainEvents();

        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("TokenRevoked", events.get(0).getClass().getSimpleName());
        Assertions.assertTrue(accessToken.isRevoked());
    }

    @Test
    void shouldRotateTokenAndEmitEvents() {
        Session session = new Session(sessionId, userId);
        session.pullDomainEvents();
        session.addToken(accessToken);
        session.pullDomainEvents();

        Token newAccessToken = new Token(JwtId.generate(), TokenType.ACCESS, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        session.rotateToken(jwtIdAccess, newAccessToken);
        List<SessionEvent> events = session.pullDomainEvents();

        // Se espera: TokenRevoked + TokenIssued + TokenRotated
        Assertions.assertEquals(3, events.size());
        Assertions.assertEquals("TokenRotated", events.get(2).getClass().getSimpleName());
        Assertions.assertTrue(accessToken.isRevoked());
        Assertions.assertTrue(session.getTokens().contains(newAccessToken));
    }

    @Test
    void shouldRevokeSessionAndEmitEvent() {
        Session session = new Session(sessionId, userId);
        session.pullDomainEvents();

        session.revokeSession();
        List<SessionEvent> events = session.pullDomainEvents();

        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("SessionRevoked", events.get(0).getClass().getSimpleName());
        Assertions.assertTrue(session.isRevoked());
    }

    // ❌ ---------------------- Casos negativos ----------------------

    @Test
    void shouldThrowWhenAddingDuplicateToken() {
        Session session = new Session(sessionId, userId);
        session.pullDomainEvents();

        session.addToken(accessToken);

        Exception exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            session.addToken(accessToken);
        });

        Assertions.assertEquals("Token with same JwtId already exists in session.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenRevokingAlreadyRevokedToken() {
        Session session = new Session(sessionId, userId);
        session.pullDomainEvents();

        session.addToken(accessToken);
        accessToken.revoke(); // simular ya revocado

        Exception exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            session.revokeTokenByJti(jwtIdAccess);
        });

        Assertions.assertEquals("Token is already revoked.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenRevokingAlreadyRevokedSession() {
        Session session = new Session(sessionId, userId);
        session.pullDomainEvents();

        session.revokeSession();

        Exception exception = Assertions.assertThrows(IllegalStateException.class, session::revokeSession);
        Assertions.assertEquals("Session already revoked.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenRotatingNonExistentToken() {
        Session session = new Session(sessionId, userId);
        session.pullDomainEvents();

        Token newToken = new Token(JwtId.generate(), TokenType.ACCESS, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        Exception exception = Assertions.assertThrows(NoSuchElementException.class, () -> {
            session.rotateToken(JwtId.generate(), newToken);
        });

        Assertions.assertTrue(exception.getMessage().contains("Token not found for jti"));
    }
}
