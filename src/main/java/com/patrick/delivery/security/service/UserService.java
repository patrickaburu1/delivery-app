package com.patrick.delivery.security.service;

import com.patrick.delivery.entities.Users;
import com.patrick.delivery.models.LoginRequest;
import com.patrick.delivery.utils.ResponseModel;

public interface UserService {

    public ResponseModel webSignin(LoginRequest request);

    public ResponseModel<Users> findLoggedInUser();
}
