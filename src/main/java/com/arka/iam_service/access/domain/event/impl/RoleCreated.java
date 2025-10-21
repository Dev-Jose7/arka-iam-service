package com.arka.iam_service.access.domain.event.impl;

import com.arka.iam_service.access.domain.event.AccessEvent;
import com.arka.iam_service.access.domain.model.vo.RoleId;
import com.arka.iam_service.access.domain.model.vo.RoleName;

import java.time.Instant;

public record RoleCreated(
        RoleId roleId,
        RoleName roleName,
        Instant occurredAt)
implements AccessEvent {
}
