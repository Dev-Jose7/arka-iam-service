package com.arka.iam_service.access.domain.event.impl;

import com.arka.iam_service.access.domain.event.AccessEvent;
import com.arka.iam_service.access.domain.model.vo.Permission;
import com.arka.iam_service.access.domain.model.vo.RoleId;

import java.time.Instant;
import java.util.Set;

public record RolePermissionChanged(
        RoleId roleId,
        Set<Permission> newPermissions,
        Instant occurredAt
) implements AccessEvent {}

