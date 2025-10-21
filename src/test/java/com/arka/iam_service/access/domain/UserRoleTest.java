package com.arka.iam_service.access.domain;

import com.arka.iam_service.access.domain.model.entity.UserRole;
import com.arka.iam_service.access.domain.model.vo.RoleId;
import com.arka.iam_service.access.domain.model.vo.UserId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserRoleTest {

    private UserId userId;
    private RoleId roleId;
    private LocalDateTime now;

    @BeforeEach
    public void setup() {
        userId = UserId.generate();
        roleId = RoleId.generate();
        now = LocalDateTime.now();
    }

    @Test
    public void shouldCreateUserRoleSuccessfully() {
        UserRole userRole = new UserRole(userId, roleId, now);

        Assertions.assertEquals(userId, userRole.getUserId());
        Assertions.assertEquals(roleId, userRole.getRoleId());
        Assertions.assertEquals(now, userRole.getAssignedAt());
    }

    @Test
    public void shouldThrowWhenUserIdIsNull() {

        Exception exception = Assertions.assertThrows(NullPointerException.class, () -> {
            new UserRole(null, roleId, now);
        });

        Assertions.assertEquals("UserId cannot be null.", exception.getMessage());
    }

    @Test
    public void shouldThrowWhenRoleIdIsNull() {

        Exception exception = assertThrows(NullPointerException.class, () -> {
            new UserRole(userId, null, now);
        });

        Assertions.assertEquals("RoleId cannot be null.", exception.getMessage());
    }

    @Test
    public void shouldThrowWhenAssignedAtIsNull() {

        Exception exception = assertThrows(NullPointerException.class, () -> {
            new UserRole(userId, roleId, null);
        });

        Assertions.assertEquals("AssignedAt cannot be null.", exception.getMessage());
    }
}
