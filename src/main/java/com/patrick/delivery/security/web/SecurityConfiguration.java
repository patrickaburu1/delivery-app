package com.patrick.delivery.security.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@Order(2)
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, proxyTargetClass = true, prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private DeliveryWebAuthenticationProvider deliveryWebAuthenticationProvider;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/signin").permitAll()
                .antMatchers("/password-reset").permitAll()
                .antMatchers("/password-reset/*").permitAll()
                .antMatchers("/tools/*").permitAll()
                .antMatchers("/new-password/*").permitAll().anyRequest()
                .authenticated().and()
                .formLogin().loginPage("/login")
                .usernameParameter("email").passwordParameter("password").permitAll()
                .failureUrl("/login?error")
                .successHandler(successHandler())
                .and().logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .and().rememberMe()
                .tokenValiditySeconds(60 * 2)
                .and().exceptionHandling().accessDeniedPage("/access_denied")
                .and().csrf().disable()
                .sessionManagement()
                .invalidSessionUrl("/login?expired")
                .maximumSessions(1)
                .expiredUrl("/login?expired");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring().antMatchers("/resources/static/**", "/css/**", "/scss/**", "/images/**", "/js/**", "/fonts/**", "/plugins/**", "/theme/**")
                .antMatchers("/fonts.googleapis.com/**", "/.i.pravatar.cc", "/maxcdn.bootstrapcdn.com/**");

    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new LoginSuccessHandler();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(deliveryWebAuthenticationProvider);
    }


    /**
     * Override default role prefix(ROLE_somerole)
     *
     * @return
     */
    @Bean
    public static DefaultRolesPrefixPostProcessor defaultRolesPrefixPostProcessor() {
        return new DefaultRolesPrefixPostProcessor();
    }
}
