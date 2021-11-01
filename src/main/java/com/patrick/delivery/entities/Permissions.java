package com.patrick.delivery.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 * @author patrick on 6/6/20
 * @project sprintel-delivery
 */

@Entity
@Table(name = "permissions")
public class Permissions extends AuditableWithId implements Serializable {

    @Size(max = 45)
    @Column(name = "action_code", length = 45)
    private String actionCode;

    @Column(name = "action_name", nullable = false, length = 100)
    private String actionName;

    @Column(name = "role_no")
    private Long roleNo;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<UserGroups> groups;

    @JoinColumn(name = "role_no", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private AppRoles role;

    public Permissions() {
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public Long getRoleNo() {
        return roleNo;
    }

    public void setRoleNo(Long roleNo) {
        this.roleNo = roleNo;
    }



    @JsonIgnore
    public AppRoles getRole() {
        return this.role;
    }

    @JsonIgnore
    public Set<UserGroups> getGroups() {
        return this.groups;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }


}

