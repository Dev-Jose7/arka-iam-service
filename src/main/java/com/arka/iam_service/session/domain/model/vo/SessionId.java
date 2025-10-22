package com.arka.iam_service.session.domain.model.vo;

import java.util.Objects;
import java.util.UUID;

public final class SessionId {
    private final UUID value;

    private SessionId(UUID value) {
        this.value = Objects.requireNonNull(value, "SessionId cannot be null.");
    }

    public static SessionId generate() {
        return new SessionId(UUID.randomUUID());
    }

    public static SessionId parse(String value) {
        return new SessionId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    public String asString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionId sessionId = (SessionId) o;
        return Objects.equals(value, sessionId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "SessionId{" +
                "value=" + value +
                '}';
    }
}
