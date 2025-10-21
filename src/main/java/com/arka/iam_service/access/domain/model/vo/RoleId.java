package com.arka.iam_service.access.domain.model.vo;

import java.util.Objects;
import java.util.UUID;

public final class RoleId {

    private final UUID value;

    private RoleId(UUID value) {
        this.value = Objects.requireNonNull(value, "RoleId cannot be null.");
    }

    public static RoleId generate() {
        return new RoleId(UUID.randomUUID());
    }

    public static RoleId parse(String value) {
        return new RoleId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleId roleId = (RoleId) o;
        return Objects.equals(value, roleId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "RoleId{" +
                "value=" + value +
                '}';
    }
}
