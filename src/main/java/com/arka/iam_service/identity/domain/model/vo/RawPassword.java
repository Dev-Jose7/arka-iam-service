package com.arka.iam_service.identity.domain.model.vo;

import java.util.Objects;

public class RawPassword {

    private final String value;

    private RawPassword(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawPassword that = (RawPassword) o;
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
