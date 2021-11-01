package com.patrick.delivery.models;

import org.springframework.lang.NonNull;

/**
 * @author patrick on 6/10/20
 * @project sprintel-delivery
 */
public class AssignOrderReq {

    @NonNull
    private Long id;
    @NonNull
    private Long driver;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDriver() {
        return driver;
    }

    public void setDriver(Long driver) {
        this.driver = driver;
    }
}
