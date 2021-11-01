package com.patrick.delivery.models;

import org.springframework.lang.NonNull;

/**
 * @author patrick on 6/15/20
 * @project sprintel-delivery
 */
public class ApiResetPasswordReq {
    @NonNull
    private String email;
    @NonNull
    private String code;
    @NonNull
    private String newPassword;
    @NonNull
    private String confirmPassword;

    @NonNull
    public String getCode() {
        return code;
    }

    public void setCode(@NonNull String code) {
        this.code = code;
    }

    @NonNull
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(@NonNull String newPassword) {
        this.newPassword = newPassword;
    }

    @NonNull
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(@NonNull String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }
}
