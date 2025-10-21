package com.arka.iam_service.access.domain;

import com.arka.iam_service.access.domain.model.aggregate.Role;
import com.arka.iam_service.access.domain.model.vo.Permission;
import com.arka.iam_service.access.domain.model.vo.RoleId;
import com.arka.iam_service.access.domain.model.vo.RoleName;
import com.arka.iam_service.access.domain.service.PermissionCheckerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

class PermissionCheckerServiceTest {

    private final PermissionCheckerService service = new PermissionCheckerService();

    @Test
    void shouldReturnTrueWhenUserHasPermission() {
        Permission permission = new Permission("user", "read");
        Role role = new Role(RoleId.generate(), new RoleName("Reader"), Set.of(permission));

        boolean result = service.userHasPermission(List.of(role), permission);

        Assertions.assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotHavePermission() {
        Permission permission = new Permission("user", "read");
        Role role = new Role(RoleId.generate(), new RoleName("Empty"), Set.of());

        boolean result = service.userHasPermission(List.of(role), permission);

        Assertions.assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenNoRolesProvided() {
        Permission permission = new Permission("user", "read");

        boolean result = service.userHasPermission(List.of(), permission);

        Assertions.assertFalse(result);
    }

    @Test
    void shouldThrowWhenPermissionIsNull() {
        Role role = new Role(RoleId.generate(), new RoleName("Reader"), Set.of());

        Exception exception = Assertions.assertThrows(NullPointerException.class, () -> {
            service.userHasPermission(List.of(role), null);
        });

        Assertions.assertEquals("PermissionId cannot be null.", exception.getMessage());
    }
}

