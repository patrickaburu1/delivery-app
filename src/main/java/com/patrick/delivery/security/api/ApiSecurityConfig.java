package com.patrick.delivery.security.api;

import com.patrick.delivery.security.service.CustomApiUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author patrick
 * @project
 */
@Configuration
@Order(1)
@EnableWebSecurity
public class ApiSecurityConfig  extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private DeliveryAuthenticationProvider deliveryAuthenticationProvider;

    @Autowired
    @Qualifier("customApiUserDetailsService")
    private CustomApiUserDetailsService userDetailsService;

    @Autowired
    TokenHelper tokenHelper;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .antMatcher("/api/**")
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS )
                .and()
                .exceptionHandling().authenticationEntryPoint( authenticationEntryPoint )
                .and()
                .authorizeRequests()
                .antMatchers("/api/v1/login", "/api/v1/forgot-password",
                        "/api/v1/reset-password","/api/v1/order/track").permitAll()
                .anyRequest().authenticated()
                .and()

               .addFilterBefore(new TokenAuthenticationFilter(tokenHelper, userDetailsService), UsernamePasswordAuthenticationFilter.class);
                http.csrf().disable();

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        auth.authenticationProvider(deliveryAuthenticationProvider);
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }

}

