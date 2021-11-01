package com.patrick.delivery;

import com.patrick.delivery.utils.AppErrorViewResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class SprintelDeliveryApplication implements WebMvcConfigurer {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("EAT"));
    }

    /**
     * Application Locale Resolver
     *
     * @return SessionLocaleResolver
     */
    @Bean
    public LocaleResolver localResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

    @Bean
    public ErrorViewResolver errorViewResolver() {
        return new AppErrorViewResolver();
    }

    /**
     * Application LocaleChangeInterceptor: allows swapping of the default
     * locale without restarting the application. This works by modifying the
     * request accept headers
     *
     * @return LocaleChangeInterceptor
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("language");
        return interceptor;
    }
    public static void main(String[] args) {
        SpringApplication.run(SprintelDeliveryApplication.class, args);
    }

    /**
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }
}
