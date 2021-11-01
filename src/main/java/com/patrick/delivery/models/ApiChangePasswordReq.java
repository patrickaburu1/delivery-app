package com.patrick.delivery.models;

import org.springframework.lang.NonNull;

/**
 * @author patrick on 6/15/20
 * @project sprintel-delivery
 */
public class ApiChangePasswordReq {
    @NonNull
    private String oldPassword,newPassword;

    @NonNull
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(@NonNull String oldPassword) {
        this.oldPassword = oldPassword;
    }

    @NonNull
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(@NonNull String newPassword) {
        this.newPassword = newPassword;
    }
}
