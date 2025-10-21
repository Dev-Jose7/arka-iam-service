package com.arka.iam_service.access.domain.model.entity;

import com.arka.iam_service.access.domain.model.vo.RoleId;
import com.arka.iam_service.access.domain.model.vo.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserRole {

    private final UserId userId;
    private final RoleId roleId;
    private final LocalDateTime assignedAt;

    public UserRole(UserId userId, RoleId roleId, LocalDateTime assignedAt) {
        this.userId = Objects.requireNonNull(userId, "UserId cannot be null.");
        this.roleId = Objects.requireNonNull(roleId, "RoleId cannot be null.");
        this.assignedAt = Objects.requireNonNull(assignedAt, "AssignedAt cannot be null.");
    }

    public UserId getUserId() {
        return userId;
    }

    public RoleId getRoleId() {
        return roleId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
}
