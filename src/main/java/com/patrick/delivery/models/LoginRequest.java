package com.patrick.delivery.models;

import org.springframework.lang.NonNull;

/**
 * @author patrick on 6/6/20
 * @project sprintel-delivery
 */
public class LoginRequest {
    @NonNull
    private String email, password;
    @NonNull
    private String firebaseToken;

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
