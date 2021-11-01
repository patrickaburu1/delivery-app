package com.patrick.delivery.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author patrick on 6/6/20
 * @project sprintel-delivery
 */

@Entity
@Table(name = "user_groups")
public class UserGroups extends AuditableWithId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "group_name", length = 100)
    private String groupName;

    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "deleted_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedOn;

    @Column(name = "flag")
    private String flag;

    @Column(name = "base_type", length = 100)
    private String baseType;

    @Column(name="system_defined")
    private boolean systemDefined;

    @Column(name = "reason_no")
    private Long reasonNo;

    @Column(name = "reason_description", length = 200)
    private String reasonDescription;

    @JoinTable(name = "group_permissions", joinColumns = {
            @JoinColumn(name = "USER_GROUP_ID", referencedColumnName = "ID", nullable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "PERMISSION_ID", referencedColumnName = "ID", nullable = false)})
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Permissions> permissions;

    public UserGroups() { }
    public UserGroups(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Date getDeletedOn() {
        return deletedOn;
    }

    public void setDeletedOn(Date deletedOn) {
        this.deletedOn = deletedOn;
    }

    public String getFlag() { return flag; }
    public UserGroups setFlag(String flag) {
        this.flag = flag;
        return this;
    }

    public String getBaseType() { return baseType;}
    public UserGroups setBaseType(String baseType) {
        this.baseType = baseType;
        return this;
    }

    public boolean isSystemDefined() { return systemDefined; }
    public void setSystemDefined(boolean systemDefined) {
        this.systemDefined = systemDefined;
    }

    public Long getReasonNo() { return reasonNo; }
    public UserGroups setReasonNo(Long reasonNo) {
        this.reasonNo = reasonNo;
        return this;
    }

    public String getReasonDescription() {
        return reasonDescription;
    }

    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    public Set<Permissions> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permissions> permissions) {
        this.permissions = permissions;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserGroups)) {
            return false;
        }
        UserGroups other = (UserGroups) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UserGroups[ id=" + id + " ]";
    }

}
