package com.patrick.delivery.models;

import org.springframework.lang.NonNull;

/**
 * @author patrick on 6/16/20
 * @project sprintel-delivery
 */
public class ApiDriverStatusReq {
    @NonNull
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
