package com.arka.iam_service.access.domain.model.vo;

import java.util.Objects;

public final class Permission {

    private final String resource;
    private final String action;

    public Permission(String resource, String action) {
        if (resource.isBlank() || action.isBlank()) {
            throw new IllegalArgumentException("Resource and Action must not be blank");
        }

        if (resource.length() < 3 || action.length() < 3) {
            throw new IllegalArgumentException("Resource and Action must not be less than 3 characters.");
        }

        this.resource = Objects.requireNonNull(resource, "Resource cannot be null.");
        this.action = Objects.requireNonNull(action, "Action cannot be null.");
    }

    public String getResource() {
        return resource;
    }

    public String getAction() {
        return action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(resource, that.resource) && Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, action);
    }

    @Override
    public String toString() {
        return resource + "." + action;
    }
}
