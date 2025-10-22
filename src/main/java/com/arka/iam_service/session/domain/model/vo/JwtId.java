package com.arka.iam_service.session.domain.model.vo;

import java.util.Objects;
import java.util.UUID;

public final class JwtId {

    private final UUID value;

    private JwtId(UUID value) {
        this.value = Objects.requireNonNull(value, "JwtId cannot be null.");
    }

    public static JwtId generate() {
        return new JwtId(UUID.randomUUID());
    }

    public static JwtId of(String value) {
        return new JwtId(UUID.fromString(value));
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
        JwtId jwtId = (JwtId) o;
        return Objects.equals(value, jwtId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "JwtId{" +
                "value='" + value + '\'' +
                '}';
    }
}
