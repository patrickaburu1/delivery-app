/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patrick.delivery.security.web;

import com.patrick.delivery.security.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 *
 * @author David
 *
 * To God be the glory
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AuditAwareConfig {

    @Autowired
    UserServiceImpl usersService;

    @Bean
    public AuditorAware<Long> auditorAware() {

        return new AuditorAwareImpl(usersService);

    }

}
