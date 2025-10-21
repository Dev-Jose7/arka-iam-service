package com.arka.iam_service.access.domain.model.vo;

import java.util.Objects;

public class RoleName {

    private final String value;

    public RoleName(String value) {
        if (value.isBlank()) throw new IllegalArgumentException("Value must not be blank");
        if (value.length() < 3)  throw new IllegalArgumentException("Role name must be at least 3 characters");

        this.value = Objects.requireNonNull(value, "Role name cannot be null");
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleName roleName = (RoleName) o;
        return Objects.equals(value, roleName.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "RoleName{" +
                "value='" + value + '\'' +
                '}';
    }
}
