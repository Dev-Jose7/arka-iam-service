package com.arka.iam_service.identity.domain.model.vo;

import java.util.Objects;
import java.util.UUID;

public final class CredentialId {

    private final UUID value;

    private CredentialId(UUID value) {
        this.value = Objects.requireNonNull(value, "CredentialId cannot be null");
    }

    public static CredentialId create(UUID value) {
        return new CredentialId(value);
    }

    public static CredentialId parse(String value) {
        return new CredentialId(UUID.fromString(value));
    }

    public UUID getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CredentialId that = (CredentialId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "CredentialId{" +
                "value=" + value +
                '}';
    }
}
