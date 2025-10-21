package com.arka.iam_service.access.domain.service;

import com.arka.iam_service.access.domain.model.aggregate.Role;
import com.arka.iam_service.access.domain.model.vo.Permission;

import java.util.List;
import java.util.Objects;

public class PermissionCheckerService {

    public boolean userHasPermission(List<Role> userRoles, Permission permission) {
        Objects.requireNonNull(permission, "PermissionId cannot be null.");

        return userRoles.stream()
                .anyMatch(role -> role.hasPermission(permission));
    }
}

