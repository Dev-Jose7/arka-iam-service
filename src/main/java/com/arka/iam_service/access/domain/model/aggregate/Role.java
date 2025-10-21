package com.arka.iam_service.access.domain.model.aggregate;

import com.arka.iam_service.access.domain.event.AccessEvent;
import com.arka.iam_service.access.domain.event.impl.RoleCreated;
import com.arka.iam_service.access.domain.event.impl.RolePermissionChanged;
import com.arka.iam_service.access.domain.event.impl.RoleRenamed;
import com.arka.iam_service.access.domain.model.vo.Permission;
import com.arka.iam_service.access.domain.model.vo.RoleId;
import com.arka.iam_service.access.domain.model.vo.RoleName;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

public class Role {

    private final RoleId id;
    private RoleName name;
    private final Set<Permission> permissions = new HashSet<>();
    private final List<AccessEvent> domainEvents = new ArrayList<>();

    public Role(RoleId id, RoleName name, Set<Permission> permissions) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);

        if (permissions != null) {
            this.permissions.addAll(permissions);
        }

        this.domainEvents.add(new RoleCreated(this.id, this.name, Instant.now()));
    }

    public void addPermission(Permission permission) {
        if (!permissions.add(permission)) {
            throw new IllegalStateException("Permission already exists in this role.");
        }
        this.domainEvents.add(new RolePermissionChanged(id, getPermissions(), Instant.now()));
    }

    public void removePermission(Permission permission) {
        if (!permissions.remove(permission)) {
            throw new IllegalStateException("Permission does not exist in this role.");
        }
        this.domainEvents.add(new RolePermissionChanged(id, getPermissions(), Instant.now()));
    }

    public void rename(RoleName newName) {
        if (this.name.equals(newName)) {
            throw new IllegalStateException("New role name is the same as current name.");
        }
        this.name = newName;
        this.domainEvents.add(new RoleRenamed(id, newName, Instant.now()));
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }


    public List<AccessEvent> pullDomainEvents() {
        List<AccessEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }

    public RoleId getId() {
        return id;
    }

    public RoleName getName() {
        return name;
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(this.permissions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name=" + name +
                ", permissions=" + permissions +
                '}';
    }
}

