package com.patrick.delivery.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author patrick on 7/21/20
 * @project sprintel-delivery
 */
@Entity
@Table(name = "app_roles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"role_code"})
})
public class AppRoles  extends AuditableWithId implements Serializable {

    private static final long serialVersionUID = 1L;

    // The unique role code
    @Column(name = "role_code", nullable = false, length = 45)
    private String roleCode;

    // the user friendly role name
    @Column(name = "role_name", nullable = false, length = 100)
    private String roleName;

    // A brief description of the role
    @Column(name = "description", nullable = true, length = 250)
    private String description;

    // A list of the mf types that this role falls under
    @Column(name = "app_functions", nullable = false, length = 100)
    private String appFunctions;


    // users who have been assigned this role
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "role")
    private Set<Permissions> permissions = new HashSet<Permissions>();

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAppFunctions() {
        return appFunctions;
    }

    public void setAppFunctions(String appFunctions) {
        this.appFunctions = appFunctions;
    }

    public Set<Permissions> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permissions> permissions) {
        this.permissions = permissions;
    }
}
