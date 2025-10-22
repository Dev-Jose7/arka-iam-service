package com.arka.iam_service.session.domain.model.aggregate;

import com.arka.iam_service.session.domain.event.SessionEvent;
import com.arka.iam_service.session.domain.event.impl.*;
import com.arka.iam_service.session.domain.model.entity.Token;
import com.arka.iam_service.session.domain.model.vo.JwtId;
import com.arka.iam_service.session.domain.model.vo.SessionId;

import java.time.Instant;
import java.util.*;

public class Session {

    private final SessionId id;
    private final UUID userId;
    private final List<Token> tokens = new ArrayList<>();
    private boolean revoked;
    private final List<SessionEvent> domainEvents = new ArrayList<>();

    public Session(SessionId id, UUID userId) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.revoked = false;

        this.domainEvents.add(new SessionStarted(id, Instant.now()));
    }

    public void addToken(Token token) {
        if (tokens.stream().anyMatch(t -> t.getJwtId().equals(token.getJwtId()))) {
            throw new IllegalStateException("Token with same JwtId already exists in session.");
        }

        tokens.add(token);
        domainEvents.add(new TokenIssued(token.getJwtId(), Instant.now()));
    }

    public void revokeTokenByJti(JwtId jwtId) {
        Token token = findToken(jwtId);
        token.revoke();
        domainEvents.add(new TokenRevoked(jwtId, Instant.now()));
    }

    public void rotateToken(JwtId oldJti, Token newToken) {
        revokeTokenByJti(oldJti);
        addToken(newToken);
        domainEvents.add(new TokenRotated(oldJti, newToken.getJwtId(), Instant.now()));
    }

    public void revokeSession() {
        if (revoked) {
            throw new IllegalStateException("Session already revoked.");
        }

        this.revoked = true;
        domainEvents.add(new SessionRevoked(id, Instant.now()));
    }

    public boolean isRevoked() {
        return revoked;
    }

    public SessionId getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public List<Token> getTokens() {
        return Collections.unmodifiableList(tokens);
    }

    public List<SessionEvent> pullDomainEvents() {
        List<SessionEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    private Token findToken(JwtId jwtId) {
        return tokens.stream()
                .filter(t -> t.getJwtId().equals(jwtId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Token not found for jti: " + jwtId.getValue()));
    }
}
