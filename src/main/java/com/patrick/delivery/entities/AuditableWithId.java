/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patrick.delivery.entities;

import javax.persistence.*;

/**
 *
 * @author David
 *
 * To God be the glory
 */
@MappedSuperclass
public abstract class AuditableWithId extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(columnDefinition = "varchar(20) default 'active'", nullable = false)
    private String status;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
