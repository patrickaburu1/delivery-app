package com.patrick.delivery.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import javax.persistence.*;

/**
 * @author patrick on 6/6/20
 * @project sprintel-delivery
 */
@Entity
@Table(name = "users")
public class Users extends AuditableWithId {
    private static final long serialVersionUID = 1L;

    @Column(name = "first_name", length = 45)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "email", length = 250)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "flag")
    private String flag;

    @Column(name = "firabase_token", length = 250)
    private String fireBaseToken;

    @Column(name = "user_group")
    private Long usergroupNo;

    @Column(name = "user_type")
    private Long userTypeNo;

    @JoinColumn(name = "user_group", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private UserGroups userGroupLink;

    @JoinColumn(name = "user_type", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private UserTypes userTypeLink;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public Users setPassword(String password) {
        // Check if the bcrypt encoding has been set
        if (!StringUtils.isEmpty(password)) {
            if (!password.startsWith("$2a$10$")) {
                BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
                password = enc.encode(password);
            }
        }

        // Set the username and return this object
        this.password = password;
        return this;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Long getUsergroupNo() {
        return usergroupNo;
    }

    public void setUsergroupNo(Long usergroupNo) {
        this.usergroupNo = usergroupNo;
    }

    public Long getUserTypeNo() {
        return userTypeNo;
    }

    public void setUserTypeNo(Long userTypeNo) {
        this.userTypeNo = userTypeNo;
    }

    public String getFireBaseToken() {
        return fireBaseToken;
    }

    public void setFireBaseToken(String fireBaseToken) {
        this.fireBaseToken = fireBaseToken;
    }

    public UserGroups getUserGroupLink() {
        return userGroupLink;
    }

    public UserTypes getUserTypeLink() {
        return userTypeLink;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
