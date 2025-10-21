package com.arka.iam_service.access.domain;

import com.arka.iam_service.access.domain.event.AccessEvent;
import com.arka.iam_service.access.domain.event.impl.RoleCreated;
import com.arka.iam_service.access.domain.event.impl.RolePermissionChanged;
import com.arka.iam_service.access.domain.event.impl.RoleRenamed;
import com.arka.iam_service.access.domain.model.aggregate.Role;
import com.arka.iam_service.access.domain.model.vo.Permission;
import com.arka.iam_service.access.domain.model.vo.RoleId;
import com.arka.iam_service.access.domain.model.vo.RoleName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoleTest {

    private RoleId roleId;
    private RoleName roleName;
    private Permission read;
    private Permission write;

    @BeforeEach
    void setUp() {
        roleId = RoleId.generate();
        roleName = new RoleName("Admin");
        read = new Permission("user", "read");
        write = new Permission("user", "write");
    }

    @Test
    void shouldCreateRole() {
        Role role = new Role(roleId, roleName, Set.of(read));

        Assertions.assertEquals(roleName, role.getName());
        assertTrue(role.getPermissions().contains(read));

        List<AccessEvent> events = role.pullDomainEvents();
        Assertions.assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof RoleCreated);
    }

    @Test
    void shouldAddPermission() {
        Role role = new Role(roleId, roleName, Set.of());
        role.pullDomainEvents(); // limpiar eventos iniciales

        role.addPermission(write);

        Assertions.assertTrue(role.getPermissions().contains(write));

        List<AccessEvent> events = role.pullDomainEvents();
        Assertions.assertEquals(1, events.size());
        Assertions.assertTrue(events.get(0) instanceof RolePermissionChanged);
    }

    @Test
    void shouldRemovePermission() {
        Role role = new Role(roleId, roleName, Set.of(read));
        role.pullDomainEvents();

        role.removePermission(read);

        Assertions.assertFalse(role.getPermissions().contains(read));
        List<AccessEvent> events = role.pullDomainEvents();
        Assertions.assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof RolePermissionChanged);
    }

    @Test
    void shouldRenameRole() {
        Role role = new Role(roleId, roleName, Set.of());
        role.pullDomainEvents();

        RoleName newName = new RoleName("Manager");
        role.rename(newName);

        Assertions.assertEquals(newName, role.getName());
        List<AccessEvent> events = role.pullDomainEvents();
        Assertions.assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof RoleRenamed);
    }

    @Test
    void shouldReturnTrueIfPermissionExists() {
        Role role = new Role(roleId, roleName, Set.of(read));
        assertTrue(role.hasPermission(read));
    }

    @Test
    void shouldReturnFalseIfPermissionNotExists() {
        Role role = new Role(roleId, roleName, Set.of(read));
        Assertions.assertFalse(role.hasPermission(write));
    }

    @Test
    void shouldThrowWhenCreateRoleWithNullId() {
        Assertions.assertThrows(NullPointerException.class, () -> new Role(null, roleName, null));
    }

    @Test
    void shouldThrowWhenCreateRoleWithNullName() {
        Assertions.assertThrows(NullPointerException.class, () -> new Role(roleId, null, null));
    }

    @Test
    void shouldThrowWhenAddingDuplicatePermission() {
        Role role = new Role(roleId, roleName, Set.of(read));
        role.pullDomainEvents();

        Exception exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            role.addPermission(read);
        });
    }

    @Test
    void shouldThrowWhenRenamingWithSameName() {
        Role role = new Role(roleId, roleName, Set.of());
        role.pullDomainEvents();

        Exception exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            role.rename(new RoleName("Admin"));
        });
    }

    @Test
    void shouldThrowWhenRemovingNonExistentPermission() {
        Role role = new Role(roleId, roleName, Set.of());
        role.pullDomainEvents();

        Exception exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            role.removePermission(write); // no existe
        });
    }

}
