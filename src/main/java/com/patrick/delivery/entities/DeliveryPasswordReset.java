package com.patrick.delivery.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author patrick on 6/15/20
 * @project sprintel-delivery
 */
@Entity
@Table(name = "password_resets" )
public class DeliveryPasswordReset implements Serializable {
    @Id
    @Column(name = "ID", nullable = false, precision = 0, length = 11)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic
    @Column(name = "GUID", nullable = false, precision = 0)
    private String guid;


    @Basic
    @Column(name = "USER_ID", nullable = true, precision = 0)
    private Long userId;

    @Basic
    @Column(name = "TIME", nullable = false)
    private Date time;

    @Basic
    @Column(name = "EMAIL", nullable = true, length = 65)
    private String email;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeliveryPasswordReset that = (DeliveryPasswordReset) o;

        if (id != that.id) return false;
        if (!guid.equals(that.guid)) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        return email != null ? email.equals(that.email) : that.email == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}
