package com.arka.iam_service.identity.domain.model.vo;

import java.util.Objects;

public final class HashedPassword {
    private final String value;

    private HashedPassword(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be blank");
        }

        if (!value.startsWith("$2a$")) {
            throw new IllegalArgumentException("Invalid password hash format");
        }

        this.value = value;
    }

    public static HashedPassword create(String hashedValue) {
        return new HashedPassword(hashedValue);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashedPassword that = (HashedPassword) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "********";
    }
}
