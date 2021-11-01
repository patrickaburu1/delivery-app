package com.patrick.delivery.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author patrick on 6/6/20
 * @project sprintel-delivery
 */

@Entity
@Table(name = "user_type")
public class UserTypes extends AuditableWithId implements Serializable {

    public static final String SUPERADMIN = "admin";
    public static final String RIDER = "rider";

    private static final long serialVersionUID = 1L;

    @Column(name = "name", length = 30)
    private String name;


    @Column(name = "code", length = 100)
    private String code;

    public UserTypes() {}

    public String getName() {  return name;}
    public UserTypes setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() { return code;}
    public UserTypes setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }


    @Override
    public String toString() {
        return "UserType[ id=" + id + " ]";
    }

}
