package com.patrick.delivery.models;

import org.springframework.lang.NonNull;

/**
 * @author patrick on 6/15/20
 * @project sprintel-delivery
 */
public class ApiForgotPasswordReq {
    @NonNull
    private String email;
    @NonNull
    private String hashKey;

    @NonNull
    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(@NonNull String hashKey) {
        this.hashKey = hashKey;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }
}
