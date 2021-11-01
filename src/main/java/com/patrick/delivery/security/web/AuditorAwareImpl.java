/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patrick.delivery.security.web;

import com.patrick.delivery.security.service.UserService;
import com.patrick.delivery.entities.Users;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * @author David
 * <p>
 * To God be the glory
 */
public class AuditorAwareImpl implements AuditorAware<Long> {

    private UserService usersService;

    public AuditorAwareImpl(UserService usersService) {
        this.usersService = usersService;
    }

    @Override
    public Optional<Long> getCurrentAuditor() {
        final String loggedInUserName = SecurityUtils.getCurrentUserLogin();

        if (null == loggedInUserName) {
            return Optional.empty();
        }

        Users user = usersService.findLoggedInUser().getData();

        if (null == user)
            return Optional.empty();

        return Optional.of(user.getId());


    }

}
