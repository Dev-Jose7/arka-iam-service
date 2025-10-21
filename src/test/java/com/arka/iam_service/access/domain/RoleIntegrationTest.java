package com.arka.iam_service.access.domain;

import com.arka.iam_service.access.domain.event.AccessEvent;
import com.arka.iam_service.access.domain.model.aggregate.Role;
import com.arka.iam_service.access.domain.model.entity.UserRole;
import com.arka.iam_service.access.domain.model.vo.*;
import com.arka.iam_service.access.domain.service.PermissionCheckerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

class RoleIntegrationTest {

    private RoleId roleId;
    private RoleName roleName;
    private Permission readPermission;
    private Permission writePermission;
    private PermissionCheckerService permissionCheckerService;

    @BeforeEach
    void setUp() {
        roleId = RoleId.generate();
        roleName = new RoleName("Admin");
        readPermission = new Permission("user", "read");
        writePermission = new Permission("user", "write");
        permissionCheckerService = new PermissionCheckerService();
    }

    @Test
    void shouldCreateRoleAndRaiseEvent() {
        Role role = new Role(roleId, roleName, Set.of(readPermission));
        List<AccessEvent> events = role.pullDomainEvents();

        Assertions.assertEquals(1, events.size());
        Assertions.assertTrue(role.hasPermission(readPermission));
        Assertions.assertEquals("Admin", role.getName().getValue());
    }

    @Test
    void shouldAddPermissionAndRaiseEvent() {
        Role role = new Role(roleId, roleName, Set.of(readPermission));
        role.pullDomainEvents();

        role.addPermission(writePermission);
        List<AccessEvent> events = role.pullDomainEvents();

        Assertions.assertEquals(1, events.size());
        Assertions.assertTrue(role.hasPermission(writePermission));
        Assertions.assertEquals(2, role.getPermissions().size());
    }

    @Test
    void shouldRemovePermissionAndRaiseEvent() {
        Role role = new Role(roleId, roleName, new HashSet<>(Set.of(readPermission, writePermission)));
        role.pullDomainEvents();

        role.removePermission(readPermission);
        List<AccessEvent> events = role.pullDomainEvents();

        Assertions.assertEquals(1, events.size());
        Assertions.assertFalse(role.hasPermission(readPermission));
    }

    @Test
    void shouldRenameRoleAndRaiseEvent() {
        Role role = new Role(roleId, roleName, Set.of(readPermission));
        role.pullDomainEvents();

        RoleName newName = new RoleName("Manager");
        role.rename(newName);
        List<AccessEvent> events = role.pullDomainEvents();

        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("Manager", role.getName().getValue());
    }

    @Test
    void shouldCheckUserHasPermissionThroughService() {
        Role role = new Role(roleId, roleName, Set.of(readPermission, writePermission));
        List<Role> roles = List.of(role);

        boolean result = permissionCheckerService.userHasPermission(roles, readPermission);
        Assertions.assertTrue(result);
    }

    @Test
    void shouldAssignUserRole() {
        UserId userId = UserId.generate();
        RoleId roleId = RoleId.generate();
        LocalDateTime assignedAt = LocalDateTime.now();

        UserRole userRole = new UserRole(userId, roleId, assignedAt);

        Assertions.assertEquals(userId, userRole.getUserId());
        Assertions.assertEquals(roleId, userRole.getRoleId());
        Assertions.assertEquals(assignedAt, userRole.getAssignedAt());
    }

    @Test
    void shouldThrowWhenAddingDuplicatePermission() {
        Role role = new Role(roleId, roleName, Set.of(readPermission));
        role.pullDomainEvents();

        Assertions.assertThrows(IllegalStateException.class, () -> role.addPermission(readPermission));
        List<AccessEvent> events = role.pullDomainEvents();
        Assertions.assertEquals(0, events.size());
    }

    @Test
    void shouldThrowWhenRemovingNonExistentPermission() {
        Role role = new Role(roleId, roleName, Set.of(readPermission));
        role.pullDomainEvents();

        Assertions.assertThrows(IllegalStateException.class, () -> role.removePermission(writePermission));
        List<AccessEvent> events = role.pullDomainEvents();
        Assertions.assertEquals(0, events.size());
    }

    @Test
    void shouldThrowWhenRenamingWithSameName() {
        Role role = new Role(roleId, roleName, Set.of(readPermission));
        role.pullDomainEvents();

        Assertions.assertThrows(IllegalStateException.class, () -> role.rename(new RoleName("Admin")));
        List<AccessEvent> events = role.pullDomainEvents();
        Assertions.assertEquals(0, events.size());
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotHavePermission() {
        Role role = new Role(roleId, roleName, Set.of(readPermission));
        List<Role> roles = List.of(role);

        boolean result = permissionCheckerService.userHasPermission(roles, new Permission("order", "delete"));
        Assertions.assertFalse(result);
    }
}
