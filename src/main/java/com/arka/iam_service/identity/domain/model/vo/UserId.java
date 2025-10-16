package com.arka.iam_service.identity.domain.model.vo;

import java.util.Objects;
import java.util.UUID;

public final class UserId {
    private final UUID value;

    private UserId(UUID value) {
        this.value = Objects.requireNonNull(value, "UserId cannot be null");
    }

    public static UserId create(UUID value) {
        return new UserId(value);
    }

    public static UserId parse(String value) {
        return new UserId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "UserId{" +
                "value=" + value +
                '}';
    }
}
