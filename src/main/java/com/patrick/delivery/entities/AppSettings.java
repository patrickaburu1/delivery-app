package com.patrick.delivery.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author patrick on 6/29/20
 * @project sprintel-delivery
 */
@Entity
@Table(name = "app_settings")
public class AppSettings extends AuditableWithId implements Serializable {

    public static final String APP_SETTINGS_MAX_ORDER_CAN_BE_ASSIGNED="DRIVER_MAX_ASSIGNED_ORDERS";

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "value")
    private String value;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
