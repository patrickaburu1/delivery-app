/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.patrick.delivery.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author David
 * <p>
 * To God be the glory
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable implements Serializable {

    @JsonIgnore
    @CreatedBy
    @Column(name = "created_by_Id")
    protected Long createdById;

    @JsonIgnore
    @LastModifiedBy
    @Column(name = "last_modified_by_id")
    protected Long lastModifiedById;

    @JsonIgnore
    @CreatedDate
    @Column(name = "creation_date")
    @Temporal(value = TemporalType.TIMESTAMP)
    protected Date creationDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    @Temporal(value = TemporalType.TIMESTAMP)
    protected Date lastModifiedDate;

    @JsonIgnore
    @JoinColumn(name = "created_by_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne
    protected Users createdByUser;

    @JsonIgnore
    @JoinColumn(name = "last_modified_by_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne
    protected Users lastModifiedByUser;

    public abstract Long getId();

    public abstract void setId(Long id);


    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }


    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public Long getLastModifiedById() {
        return lastModifiedById;
    }

    public void setLastModifiedById(Long lastModifiedById) {
        this.lastModifiedById = lastModifiedById;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Users getCreatedByUser() {
        return createdByUser;
    }


    public Users getLastModifiedByUser() {
        return lastModifiedByUser;
    }

}
